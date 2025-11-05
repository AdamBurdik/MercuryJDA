package me.adamix.mercury.jda.components.modal;

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ModalComponent(
		@NotNull String id,
		@NotNull String title,
		@NotNull List<TextInput> inputList,
		int argHash
) {
	public static final String PREFIX = "mercury";

	public @NotNull Modal toJda() {
		return Modal.create(id + ":" + argHash, title)
				.addComponents(
						inputList.stream()
								.map(ActionRow::of)
								.toList()
				)
				.build();
	}
}
