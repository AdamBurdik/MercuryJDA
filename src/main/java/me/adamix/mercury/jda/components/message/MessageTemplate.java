package me.adamix.mercury.jda.components.message;

import me.adamix.mercury.jda.components.message.button.handler.ButtonHandler;
import me.adamix.mercury.jda.components.message.select.handler.SelectHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MessageTemplate {
	private final Map<@NotNull String, @NotNull ButtonHandler> buttonHandlerMap;
	private final Map<@NotNull String, @NotNull SelectHandler> selectHandlerMap;
	private @Nullable Consumer<MessageInstance> initializeConsumer = null;
	private boolean hasBeenBuilt = false;

	public MessageTemplate(
			Map<@NotNull String, @NotNull ButtonHandler> buttonHandlerMap,
			Map<@NotNull String, @NotNull SelectHandler> selectHandlerMap
	) {
		this.buttonHandlerMap = buttonHandlerMap;
		this.selectHandlerMap = selectHandlerMap;
	}

	public @NotNull MessageTemplate button(
			@NotNull String id,
			@NotNull ButtonHandler handler
	) {
		buttonHandlerMap.put(id, handler);
		return this;
	}

	public @NotNull MessageTemplate select(
			@NotNull String id,
			@NotNull SelectHandler handler
	) {
		selectHandlerMap.put(id, handler);
		return this;
	}

	public @NotNull MessageTemplate initialize(@NotNull Consumer<MessageInstance> consumer) {
		this.initializeConsumer = consumer;
		return this;
	}

	public @NotNull MessageTemplate build() {
		buttonHandlerMap.forEach(MessageComponentManager::registerHandler);
		selectHandlerMap.forEach(MessageComponentManager::registerHandler);
		hasBeenBuilt = true;
		return this;
	}

	public @NotNull MessageInstance instance(long userId) {
		if (!hasBeenBuilt) throw new IllegalStateException("Please call MessageTemplate#buid() before creating an instance!");

		MessageInstance instance = new MessageInstance(new ArrayList<>(), new ArrayList<>(), userId);
		if (initializeConsumer != null) initializeConsumer.accept(instance);

		return instance;
	}

	public static @NotNull MessageTemplate create() {
		return new MessageTemplate(new HashMap<>(), new HashMap<>());
	}
}
