package me.adamix.mercury.jda.message.parser;

import me.adamix.mercury.configuration.TomlConfiguration;
import me.adamix.mercury.configuration.api.MercuryArray;
import me.adamix.mercury.configuration.api.MercuryConfiguration;
import me.adamix.mercury.configuration.api.MercuryTable;
import me.adamix.mercury.configuration.api.exception.ParsingException;
import me.adamix.mercury.jda.message.EmbedConfig;
import me.adamix.mercury.jda.message.MercuryMessage;
import me.adamix.mercury.jda.placeholder.PlaceholderManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class MessageParser {
	public static @NotNull Map<String, MercuryMessage> parse(
			@NotNull PlaceholderManager placeholderManager,
			@NotNull Path path
	) throws ParsingException, IOException {
		Map<String, MercuryMessage> messageMap = new HashMap<>();
		MercuryConfiguration config = TomlConfiguration.of(path);

		Set<String> dottedKeySet = config.dottedKeySet(true);
		for (String key : dottedKeySet) {
			if (key.endsWith(".message") || key.endsWith(".EMBED")) {
				String tablePath = key.replace(".message", "").replace(".EMBED", "");
				MercuryTable table = config.getTable(tablePath);
				if (table == null) {
					continue;
				}

				String message = table.getString("message");
				EmbedConfig embed = getEmbedConfig(table, "EMBED");
				messageMap.put(tablePath, new MercuryMessage.Success(placeholderManager, message, embed));
			}
		}
		return messageMap;
	}

	public static @Nullable EmbedConfig getEmbedConfig(@NotNull MercuryTable table, @NotNull String dottedKey) {
		if (table.getTable(dottedKey) == null) return null;

		List<EmbedConfig.Field> fields = null;
		MercuryArray fieldArray = table.getArray(dottedKey + ".fields");
		if (fieldArray != null) {
			fields = new ArrayList<>();
			for (MercuryTable fieldTable : fieldArray.toTableArray()) {
				fields.add(new EmbedConfig.Field(
						fieldTable.getString("name"),
						fieldTable.getString("value"),
						fieldTable.getBoolean("inline")
				));
			}
		}

		return new EmbedConfig(
				table.getString(dottedKey + ".title"),
				table.getString(dottedKey + ".description"),
				table.getString(dottedKey + ".url"),
				table.getString(dottedKey + ".timestamp"),
				table.getInteger(dottedKey + ".color"),
				getSubConfig(table, dottedKey + ".footer", () -> new EmbedConfig.Footer(
						table.getString(dottedKey + ".footer.text"),
						table.getString(dottedKey + ".footer.icon_url"),
						table.getString(dottedKey + ".footer.proxy_icon_url")
				)),
				getSubConfig(table, dottedKey + ".image", () -> new EmbedConfig.Image(
						table.getString(dottedKey + ".image.url"),
						table.getString(dottedKey + ".image.proxy_url"),
						table.getInteger(dottedKey + ".image.height"),
						table.getInteger(dottedKey + ".image.width")
				)),
				getSubConfig(table, dottedKey + ".thumbnail", () -> new EmbedConfig.Thumbnail(
						table.getString(dottedKey + ".thumbnail.url"),
						table.getString(dottedKey + ".thumbnail.proxy_url"),
						table.getInteger(dottedKey + ".thumbnail.height"),
						table.getInteger(dottedKey + ".thumbnail.width")
				)),
				getSubConfig(table, dottedKey + ".video", () -> new EmbedConfig.Video(
						table.getString(dottedKey + ".video.url"),
						table.getInteger(dottedKey + ".video.height"),
						table.getInteger(dottedKey + ".video.width")
				)),
				getSubConfig(table, dottedKey + ".provider", () -> new EmbedConfig.Provider(
						table.getString(dottedKey + ".provider.name"),
						table.getString(dottedKey + ".provider.url")
				)),
				getSubConfig(table, dottedKey + ".author", () -> new EmbedConfig.Author(
						table.getString(dottedKey + ".author.url"),
						table.getString(dottedKey + ".author.name"),
						table.getString(dottedKey + ".author.icon_url"),
						table.getString(dottedKey + ".author.proxy_icon_url")
				)),
				fields
		);
	}

	private static <T> T getSubConfig(@NotNull MercuryTable table, @NotNull String prefix, @NotNull Supplier<T> supplier) {
		return table.contains(prefix) ? supplier.get() : null;
	}
}
