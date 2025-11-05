package me.adamix.mercury.jda.components.modal;

import kotlin.internal.NoInfer;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModalInstance {
	private final List<TextInput> textInputList = new ArrayList<>();
	private final String[] args;
	private final long userId;
	private final @NotNull String id;
	private @Nullable String title = null;

	public ModalInstance(
			long userId,
			String[] args,
			@NotNull String id
	) {
		this.args = args;
		this.userId = userId;
		this.id = id;
	}

	public @NotNull ModalInstance title(
			@NotNull String title
	) {
		this.title = title;
		return this;
	}

	public @NotNull ModalInstance shortInput(
			@NotNull String id,
			@NotNull String label,
			@NotNull String placeholder,
			int minLength,
			int maxLength,
			boolean required
	) {
		textInputList.add(
				TextInput.create(id, label, TextInputStyle.SHORT)
						.setPlaceholder(placeholder)
						.setMinLength(minLength)
						.setMaxLength(maxLength)
						.setRequired(required)
						.build()
		);
		return this;
	}

	public @NotNull ModalComponent getAndRegisterComponent() {
		if (id == null || title == null)
			throw new IllegalStateException("Modal ID and title must be set before getting the component!");

		int argHash = ModalComponentManager.storeArgs(userId, args);

		String finalId = ModalComponent.PREFIX + ":" + userId + ":" + id;

		ModalComponent component = new ModalComponent(
				finalId,
				title,
				textInputList,
				argHash
		);

		ModalComponentManager.registerComponent(finalId, component);
		return component;
	}

	public @NotNull ModalCallbackAction send(
			@NotNull IModalCallback callback
	) {
		return callback.replyModal(getAndRegisterComponent().toJda());
	}
}
