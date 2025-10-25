package me.adamix.mercury.jda.components.message.button;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ButtonComponent(
		@NotNull String id,
		int row,
		@NotNull String label,
		@NotNull ButtonStyle style,
		boolean disabled,
		@Nullable Emoji emoji,
		boolean authorOnly,
		int argHash
) {
	public static final String PREFIX = "mercury";

	public @NotNull Button toJda() {
		return Button.of(style, id + ":" + argHash, label, emoji).withDisabled(disabled);
	}
}