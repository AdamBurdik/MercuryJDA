package me.adamix.mercury.jda.components.message.select;

import me.adamix.mercury.jda.components.message.MessageInstance;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SelectBuilder {
	private final MessageInstance instance;
	private final @NotNull String id;
	private final int row;
	private final @NotNull String placeholder;
	private final @NotNull String[] args;

	private @NotNull List<SelectOption> optionList = new ArrayList<>();
	private @NotNull List<String> defaultValueList = new ArrayList<>();
	private int minValues = 1;
	private int maxValues = 1;
	private boolean disabled = false;
	private boolean authorOnly = true;

	public SelectBuilder(
			@NotNull MessageInstance instance,
			@NotNull String id,
			int row,
			@NotNull String placeholder,
			@NotNull String... args
	) {
		this.instance = instance;
		this.id = id;
		this.row = row;
		this.placeholder = placeholder;
		this.args = args;
	}

	/**
	 * Add an option to the select menu
	 *
	 * @param label The option label (shown to user)
	 * @param value The option value (sent in interaction)
	 * @return This builder for chaining
	 */
	public @NotNull SelectBuilder option(@NotNull String label, @NotNull String value) {
		optionList.add(SelectOption.of(label, value));
		return this;
	}

	/**
	 * Add an option with description
	 *
	 * @param label The option label
	 * @param value The option value
	 * @param description The option description
	 * @return This builder for chaining
	 */
	public @NotNull SelectBuilder option(@NotNull String label, @NotNull String value, @NotNull String description) {
		optionList.add(SelectOption.of(label, value).withDescription(description));
		return this;
	}

	/**
	 * Add an option with emoji
	 *
	 * @param label The option label
	 * @param value The option value
	 * @param emoji The option emoji
	 * @return This builder for chaining
	 */
	public @NotNull SelectBuilder option(@NotNull String label, @NotNull String value, @Nullable Emoji emoji) {
		SelectOption option = SelectOption.of(label, value);
		if (emoji != null) {
			option = option.withEmoji(emoji);
		}
		optionList.add(option);
		return this;
	}

	/**
	 * Add an option with description and emoji
	 *
	 * @param label The option label
	 * @param value The option value
	 * @param description The option description
	 * @param emoji The option emoji
	 * @return This builder for chaining
	 */
	public @NotNull SelectBuilder option(@NotNull String label, @NotNull String value, @NotNull String description, @Nullable Emoji emoji) {
		SelectOption option = SelectOption.of(label, value).withDescription(description);
		if (emoji != null) {
			option = option.withEmoji(emoji);
		}
		optionList.add(option);
		return this;
	}

	/**
	 * Add an option that is selected by default
	 *
	 * @param label The option label
	 * @param value The option value
	 * @return This builder for chaining
	 */
	public @NotNull SelectBuilder defaultOption(@NotNull String label, @NotNull String value) {
		optionList.add(SelectOption.of(label, value).withDefault(true));
		defaultValueList.add(value);
		return this;
	}

	/**
	 * Add an option that is selected by default with description
	 *
	 * @param label The option label
	 * @param value The option value
	 * @param description The option description
	 * @return This builder for chaining
	 */
	public @NotNull SelectBuilder defaultOption(@NotNull String label, @NotNull String value, @NotNull String description) {
		optionList.add(SelectOption.of(label, value).withDescription(description).withDefault(true));
		defaultValueList.add(value);
		return this;
	}

	/**
	 * Set minimum number of values that must be selected
	 *
	 * @param minValues Minimum values (1-25)
	 * @return This builder for chaining
	 */
	public @NotNull SelectBuilder minValues(int minValues) {
		if (minValues < 1 || minValues > 25) {
			throw new IllegalArgumentException("minValues must be between 1 and 25");
		}
		this.minValues = minValues;
		return this;
	}

	/**
	 * Set maximum number of values that can be selected
	 *
	 * @param maxValues Maximum values (1-25)
	 * @return This builder for chaining
	 */
	public @NotNull SelectBuilder maxValues(int maxValues) {
		if (maxValues < 1 || maxValues > 25) {
			throw new IllegalArgumentException("maxValues must be between 1 and 25");
		}
		this.maxValues = maxValues;
		return this;
	}

	/**
	 * Set the select menu as disabled
	 *
	 * @param disabled Whether the select menu is disabled
	 * @return This builder for chaining
	 */
	public @NotNull SelectBuilder disabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	/**
	 * Set whether only the author can use this select menu
	 *
	 * @param authorOnly Whether only the author can interact
	 * @return This builder for chaining
	 */
	public @NotNull SelectBuilder authorOnly(boolean authorOnly) {
		this.authorOnly = authorOnly;
		return this;
	}

	/**
	 * Build and add the select menu to the instance
	 *
	 * @return The message instance for further building
	 */
	public @NotNull MessageInstance build() {
		if (optionList.isEmpty()) {
			throw new IllegalStateException("Select menu must have at least one option");
		}
		if (optionList.size() > 25) {
			throw new IllegalStateException("Select menu cannot have more than 25 options");
		}
		if (minValues > maxValues) {
			throw new IllegalStateException("minValues cannot be greater than maxValues");
		}
		if (maxValues > optionList.size()) {
			throw new IllegalStateException("maxValues cannot be greater than number of options");
		}

		return instance.stringSelect(
				id,
				row,
				placeholder,
				minValues,
				maxValues,
				optionList,
				defaultValueList,
				disabled,
				authorOnly,
				args
		);
	}
}