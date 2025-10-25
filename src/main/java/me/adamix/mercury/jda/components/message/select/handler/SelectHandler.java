package me.adamix.mercury.jda.components.message.select.handler;

import me.adamix.mercury.jda.components.message.select.SelectComponent;
import me.adamix.mercury.jda.components.result.Result;
import me.adamix.mercury.jda.context.Context;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface SelectHandler {
	@NotNull Result accept(@NotNull StringSelectInteractionEvent event, @NotNull Context<SelectComponent> ctx);
}
