package me.adamix.mercury.jda.components.modal;

import me.adamix.mercury.jda.components.result.Result;
import me.adamix.mercury.jda.context.Context;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ModalHandler {
	@NotNull Result accept(@NotNull ModalInteractionEvent event, @NotNull Context<ModalComponent> ctx);
}
