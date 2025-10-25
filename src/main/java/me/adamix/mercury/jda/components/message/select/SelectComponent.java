package me.adamix.mercury.jda.components.message.select;

import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SelectComponent(
		@NotNull String id,
		int row,
		@NotNull String placeholder,
		int minRange,
		int maxRange,
		@NotNull List<SelectOption> optionList,
		@NotNull List<String> defaultValueList,
		boolean disabled,
		boolean authorOnly,
		int argHash
) {

	public static final String PREFIX = "mercury";

	public @NotNull StringSelectMenu toJda() {
		return StringSelectMenu.create(id + ":" + argHash)
				.setPlaceholder(placeholder)
				.addOptions(optionList)
				.setRequiredRange(minRange, maxRange)
				.setDefaultValues(defaultValueList)
				.setDisabled(disabled)
				.build();
	}
}
