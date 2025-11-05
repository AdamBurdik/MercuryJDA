package me.adamix.mercury.jda.components.modal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ModalTemplate {
	private final String id;
	private @Nullable ModalHandler handler;
	private boolean hasBeenBuilt = false;
	private @Nullable Consumer<ModalInstance> initializeConsumer = null;

	public ModalTemplate(String id) {
		this.id = id;
	}

	public @NotNull ModalTemplate handler(@NotNull ModalHandler handler) {
		this.handler = handler;
		return this;
	}

	public @NotNull ModalTemplate build() {
		ModalComponentManager.registerHandler(id, handler);
		hasBeenBuilt = true;
		return this;
	}

	public @NotNull ModalTemplate initialize(@NotNull Consumer<ModalInstance> consumer) {
		this.initializeConsumer = consumer;
		return this;
	}

	public @NotNull ModalInstance instance(long userId, @NotNull String... args) {
		if (!hasBeenBuilt) throw new IllegalStateException("Please call ModalTemplate#build() before creating an instance!");

		ModalInstance instance = new ModalInstance(userId, args, id);
		if (initializeConsumer != null) initializeConsumer.accept(instance);
		return instance;
	}

	public static @NotNull ModalTemplate create(@NotNull String id) {
		return new ModalTemplate(id);
	}
}
