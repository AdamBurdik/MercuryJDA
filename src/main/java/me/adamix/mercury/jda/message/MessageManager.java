package me.adamix.mercury.jda.message;

import me.adamix.mercury.jda.message.parser.MessageParser;
import me.adamix.mercury.jda.placeholder.PlaceholderManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {
	private static final Logger logger = LoggerFactory.getLogger(MessageManager.class);
	private final @NotNull PlaceholderManager placeholderManager;
	private final Map<String, MercuryMessage> messageMap = new HashMap<>();

	public MessageManager(@NotNull PlaceholderManager placeholderManager) {
		this.placeholderManager = placeholderManager;
	}

	public void load(@NotNull Path path) {
		try {
			Map<String, MercuryMessage> result = MessageParser.parse(placeholderManager, path);
			messageMap.putAll(result);
			logger.info("Loaded {} messages!", result.size());
		} catch (Exception e) {
			logger.error("Exception occurred while loading message configuration", e);
		}
	}

	public @NotNull MercuryMessage get(@NotNull String dottedKeyPath) {
		MercuryMessage message = messageMap.get(dottedKeyPath);
		return message != null ? message : new MercuryMessage.Empty(placeholderManager, dottedKeyPath);
	}
}
