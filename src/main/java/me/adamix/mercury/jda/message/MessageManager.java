package me.adamix.mercury.jda.message;

import me.adamix.mercury.jda.placeholder.PlaceholderManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class MessageManager {
	private final @NotNull PlaceholderManager placeholderManager;
	private final Map<String, MercuryMessage> messageMap = new HashMap<>();

	public MessageManager(@NotNull PlaceholderManager placeholderManager) {
		this.placeholderManager = placeholderManager;
	}

	public @Nullable MercuryMessage get(@NotNull String dottedKeyPath) {
		return messageMap.get(dottedKeyPath);
	}
}
