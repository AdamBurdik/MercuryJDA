package me.adamix.mercury.jda.message;

import kotlin.Pair;
import me.adamix.mercury.jda.placeholder.PlaceholderManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.management.monitor.StringMonitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface MercuryMessage {
		@Nullable String message();
		@Nullable EmbedConfig embed();

		@NotNull WebhookMessageCreateAction<Message> send(@NotNull InteractionHook hook, @Nullable List<Object> args);

		@NotNull MessageCreateAction send(@NotNull MessageChannel channel, @NotNull User user, @Nullable List<Object> args);

		@NotNull Builder builder();

		record Empty(
				@NotNull PlaceholderManager placeholderManager,
				@NotNull String dottedKey
		) implements MercuryMessage {
			@Override
			public @Nullable String message() {
				return null;
			}

			@Override
			public @Nullable EmbedConfig embed() {
				return null;
			}

			@Override
			public @NotNull WebhookMessageCreateAction<Message> send(@NotNull InteractionHook hook, @Nullable List<Object> args) {
				return hook.sendMessage("Oh.. hey!\n\nsoo I haven't been able to find response for an '%s' in my configuration.\nPlease report this bug.\n\n\nHere's a cookie for you instead: :cookie:".formatted(dottedKey));
			}

			@Override
			public @NotNull MessageCreateAction send(@NotNull MessageChannel channel, @NotNull User user, @Nullable List<Object> args) {
				return channel.sendMessage(
						"Oh.. hey!\n\nsoo I haven't been able to find response for an '%s' in my configuration.\nPlease report this bug.\n\n\nHere's a cookie for you instead: :cookie:".formatted(dottedKey)
				);
			}

			@Override
			public @NotNull Builder builder() {
				return new Builder(placeholderManager, this);
			}
		}
		record Success(
				@NotNull PlaceholderManager placeholderManager,
				@Nullable String message,
				@Nullable EmbedConfig embed
		) implements MercuryMessage {
			public @NotNull Pair<@NotNull String, @Nullable MessageEmbed> getContent(
					@NotNull User user,
					@Nullable List<Object> args
			) {
				try {
					List<Object> arguments;
					if (args != null) {
						arguments = new ArrayList<>(args);
					} else {
						arguments = new ArrayList<>();
					}
					arguments.add(user);

					String parsedMessage;
					if (message != null) {
						parsedMessage = placeholderManager.parse(message, arguments);
					} else {
						parsedMessage = "";
					}
					return new Pair<>(
							parsedMessage,
							embed != null ? embed.create(placeholderManager, arguments) : null
					);
				} catch (Exception e) {
					throw new RuntimeException("Exception occurred while creating content", e);
				}
			}

			@Override
			public @NotNull WebhookMessageCreateAction<Message> send(
					@NotNull InteractionHook hook,
					@Nullable List<Object> args
			) {
				Pair<String, MessageEmbed> content = getContent(hook.getInteraction().getUser(), args);
				WebhookMessageCreateAction<Message> action = hook.sendMessage(content.getFirst());

				if (content.getSecond() != null) {
					action.addEmbeds(content.getSecond());
				}

				return action;
			}

			@Override
			public @NotNull MessageCreateAction send(
					@NotNull MessageChannel channel,
					@NotNull User user,
					@Nullable List<Object> args
			) {
				Pair<String, MessageEmbed> content = getContent(user, args);
				MessageCreateAction action = channel.sendMessage(content.getFirst());

				if (content.getSecond() != null) {
					action.addEmbeds(content.getSecond());
				}

				return action;
			}

			@Override
			public @NotNull Builder builder() {
				return new Builder(placeholderManager, this);
			}
		}

		class Builder {
			private final @NotNull PlaceholderManager placeholderManager;
			private final @NotNull MercuryMessage message;
			private final List<Object> args = new ArrayList<>();

			public Builder(@NotNull PlaceholderManager placeholderManager, @NotNull MercuryMessage message) {
				this.placeholderManager = placeholderManager;
				this.message = message;
			}

			public @NotNull Builder withArg(@NotNull Object arg) {
				args.add(arg);
				return this;
			}

			public @NotNull WebhookMessageCreateAction<Message> send(@NotNull InteractionHook hook) {
				return message.send(hook, args);
			}
		}
	}