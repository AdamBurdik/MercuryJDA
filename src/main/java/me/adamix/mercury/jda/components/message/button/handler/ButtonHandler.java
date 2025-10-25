package me.adamix.mercury.jda.components.message.button.handler;

import me.adamix.mercury.jda.components.message.button.ButtonComponent;
import me.adamix.mercury.jda.components.result.Result;
import me.adamix.mercury.jda.context.Context;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ButtonHandler {
	@NotNull Result accept(@NotNull ButtonInteractionEvent event, @NotNull Context<ButtonComponent> ctx);
}
