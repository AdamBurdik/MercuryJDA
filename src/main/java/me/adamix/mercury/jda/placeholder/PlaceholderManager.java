package me.adamix.mercury.jda.placeholder;

import me.adamix.mercury.jda.exception.ParsingException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PlaceholderManager {
	private static final Logger logger = LoggerFactory.getLogger(PlaceholderManager.class);
	private final Map<String, Entry<?>> objectPlaceholders = new HashMap<>();
	private final Map<String, GlobalPlaceholder> globalPlaceholders = new HashMap<>();

	public <T> void registerPlaceholder(@NotNull String name, @NotNull Class<T> clazz, @NotNull Placeholder<T> placeholder) {
		objectPlaceholders.put(name, new Entry<>(clazz, placeholder));
	}

	public void registerGlobalPlaceholder(@NotNull String name, @NotNull GlobalPlaceholder placeholder) {
		globalPlaceholders.put(name, placeholder);
	}

	public void registerDefaults() {
		registerGlobalPlaceholder("date", args -> {
			String pattern = "dd-MM-yyyy";
			Locale locale = Locale.ENGLISH;

			while (args.hasNext()) {
				String arg = args.pop();
				if (arg.startsWith("pattern=")) pattern = arg.substring("pattern=".length());
				else if (arg.startsWith("locale=")) locale = Locale.of(arg.substring("locale=".length()));
				else if (arg.startsWith("locale_tag=")) locale = Locale.forLanguageTag(arg.substring("locale_tag=".length()));
			}

			return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern, locale));
		});

		registerGlobalPlaceholder("time", args -> {
			StringBuilder pattern = new StringBuilder("HH:mm:ss");
			while (args.hasNext()) {
				String part = args.pop();
				if (part.startsWith("pattern=")) {
					pattern = new StringBuilder(part.substring("pattern=".length()));
					// Reconstruct extra parts that were split
					while (args.hasNext() && !args.peek().contains("=")) {
						pattern.append(":").append(args.pop());
					}
				}
			}
			return LocalTime.now().format(DateTimeFormatter.ofPattern(pattern.toString()));
		});

		registerPlaceholder("user", User.class, (args, user) -> {
			if (!args.hasNext()) return "%s(id=%s)".formatted(user.getName(), user.getId());

			String arg = args.pop().toLowerCase();
			return switch (arg) {
				case "name" -> user.getName();
				case "id" -> user.getId();
				case "globalname", "global_name" -> user.getGlobalName() != null ? user.getGlobalName() : "";
				case "displayname", "display_name" -> user.getEffectiveName();
				case "avatar_url", "avatar" -> user.getAvatarUrl() != null ? user.getAvatarUrl() : "";
				case "default_avatar_url", "default_avatar" -> user.getDefaultAvatarUrl();
				case "effective_avatar_url" -> user.getEffectiveAvatarUrl();
				case "is_bot", "bot" -> String.valueOf(user.isBot());
				case "is_system", "system" -> String.valueOf(user.isSystem());
				case "as_mention", "mention" -> user.getAsMention();
				default -> "";
			};
		});

		registerPlaceholder("text_channel", TextChannel.class, (args, channel) -> {
			if (!args.hasNext()) return channel.getName();

			String arg = args.pop().toLowerCase();
			return switch (arg) {
				case "id" -> channel.getId();
				case "name" -> channel.getName();
				case "mention" -> channel.getAsMention();
				case "topic" -> channel.getTopic() != null ? channel.getTopic() : "";
				case "nsfw" -> String.valueOf(channel.isNSFW());
				case "guild_id" -> channel.getGuild().getId();
				case "guild_name" -> channel.getGuild().getName();
				default -> "";
			};
		});

		registerPlaceholder("voice_channel", VoiceChannel.class, (args, channel) -> {
			if (!args.hasNext()) return channel.getName();

			String arg = args.pop().toLowerCase();
			return switch (arg) {
				case "id" -> channel.getId();
				case "name" -> channel.getName();
				case "user_limit" -> String.valueOf(channel.getUserLimit());
				case "bitrate" -> String.valueOf(channel.getBitrate());
				case "guild_id" -> channel.getGuild().getId();
				case "guild_name" -> channel.getGuild().getName();
				default -> "";
			};
		});

		registerPlaceholder("role", Role.class, (args, role) -> {
			if (!args.hasNext()) return role.getName();

			String arg = args.pop().toLowerCase();
			return switch (arg) {
				case "id" -> role.getId();
				case "name" -> role.getName();
				case "color" -> role.getColor() != null ? "#" + Integer.toHexString(role.getColor().getRGB()).substring(2) : "none";
				case "position" -> String.valueOf(role.getPosition());
				case "mention" -> role.getAsMention();
				case "hoisted" -> String.valueOf(role.isHoisted());
				case "managed" -> String.valueOf(role.isManaged());
				case "mentionable" -> String.valueOf(role.isMentionable());
				default -> "";
			};
		});

		registerPlaceholder("guild", Guild.class, (args, guild) -> {
			if (!args.hasNext()) return guild.getName();

			String arg = args.pop().toLowerCase();
			return switch (arg) {
				case "id" -> guild.getId();
				case "name" -> guild.getName();
				case "member_count" -> String.valueOf(guild.getMemberCount());
				case "boost_count" -> String.valueOf(guild.getBoostCount());
				case "boost_tier" -> guild.getBoostTier().name();
				case "icon_url" -> guild.getIconUrl() != null ? guild.getIconUrl() : "";
				case "owner_id" -> guild.getOwnerId();
				case "preferred_locale" -> guild.getLocale().getLocale();
				default -> "";
			};
		});

		registerPlaceholder("error", Throwable.class, (args, error) -> {
			if (!args.hasNext()) return error.toString();

			String arg = args.pop().toLowerCase();
			return switch (arg) {
				case "message", "msg" -> error.getMessage() != null ? error.getMessage() : "null";
				case "type", "class" -> error.getClass().getName();
				case "simple_class" -> error.getClass().getSimpleName();
				case "stack_trace", "stacktrace" -> {
					try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
						error.printStackTrace(pw);
						yield sw.toString();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				default -> "";
			};
		});

		registerPlaceholder("mention", String.class, (args, val) -> {
			if (!args.hasNext()) return "";

			String type = args.pop().toLowerCase();
			if (!args.hasNext()) return "";

			String value = args.pop();
			return switch (type) {
				case "channel" -> "<#%s>".formatted(value);
				default -> "";
			};
		});
	}

	public record Entry<T>(
			@NotNull Class<T> clazz,
			@NotNull Placeholder<T> placeholder
	) {}

	public String parse(@Nullable String text, @NotNull List<?> data) throws ParsingException {
		if (text == null) return null;

		StringBuilder sb = new StringBuilder();
		int index = 0;

		while (index < text.length()) {
			int start = text.indexOf('<', index);
			if (start == -1) {
				sb.append(text.substring(index));
				break;
			}

			int end = text.indexOf('>', start);
			if (end == -1) {
				sb.append(text.substring(index));
				break;
			}

			sb.append(text, index, start);

			String tagContent = text.substring(start + 1, end);
			String[] parts = tagContent.split(":", 2);
			String name = parts[0];
			String[] argSplit = parts.length > 1 ? parts[1].split(":") : new String[0];

			ArgumentQueue args = new ArgumentQueue(argSplit);
			Entry<?> entry = objectPlaceholders.get(name);
			if (entry != null) {
				boolean replaced = false;
				for (Object obj : data) {
					if (obj != null && entry.clazz.isAssignableFrom(obj.getClass())) {
						sb.append(parsePlaceholder(entry, obj, args));
						replaced = true;
						break;
					}
				}
			} else {
				GlobalPlaceholder global = globalPlaceholders.get(name);
				if (global != null) {
					sb.append(global.get(args));
				}
			}

			index = end + 1;
		}

		return sb.toString();
	}

	public <T> @NotNull String parsePlaceholder(
			@NotNull Entry<T> entry,
			@NotNull Object object,
			@NotNull ArgumentQueue args
	) throws ParsingException {
		T value = entry.clazz.cast(object);
		return entry.placeholder.get(args, value);
	}
}