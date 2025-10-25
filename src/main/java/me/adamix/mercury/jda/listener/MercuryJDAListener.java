package me.adamix.mercury.jda.listener;

import me.adamix.mercury.jda.components.message.MessageComponentManager;
import me.adamix.mercury.jda.components.message.button.handler.ButtonHandler;
import me.adamix.mercury.jda.components.result.Result;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MercuryJDAListener extends ListenerAdapter {
	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		Result result = MessageComponentManager.handleButton(event);
		if (result == Result.EXPIRED) {
			event.reply("This interaction is expired!").queue();
		} else if (result == Result.DIFFERENT_AUTHOR) {
			event.reply("This interaction is not yours!").queue();
		}
	}

	@Override
	public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
		Result result = MessageComponentManager.handleSelect(event);
		if (result == Result.EXPIRED) {
			event.reply("This interaction is expired!").queue();
		} else if (result == Result.DIFFERENT_AUTHOR) {
			event.reply("This interaction is not yours!").queue();
		}
	}
}
