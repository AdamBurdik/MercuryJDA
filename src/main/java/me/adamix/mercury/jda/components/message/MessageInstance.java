package me.adamix.mercury.jda.components.message;

import me.adamix.mercury.jda.components.message.button.ButtonComponent;
import me.adamix.mercury.jda.components.message.select.SelectBuilder;
import me.adamix.mercury.jda.components.message.select.SelectComponent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MessageInstance {
	private final @NotNull List<ButtonComponent> buttonComponents;
	private final @NotNull List<SelectComponent> selectComponents;
	private final long userId;

	public MessageInstance(
			@NotNull List<ButtonComponent> buttonComponents,
			@NotNull List<SelectComponent> selectComponents,
			long userId
	) {
		this.buttonComponents = buttonComponents;
		this.selectComponents = selectComponents;
		this.userId = userId;
	}

	private @NotNull List<List<ActionComponent>> getActionMatrix() {
		List<List<ActionComponent>> rows = new ArrayList<>();

		// Generate all row lists
		for (int i = 0; i < 5; i++) rows.add(new ArrayList<>());

		boolean register = false;
		if (!selectComponents.isEmpty()) {
			register = true;
			for (SelectComponent selectComponent : selectComponents) {

				List<ActionComponent> row = null;
				if (selectComponent.row() < 0) {
					for (List<ActionComponent> rowCandidate : rows) {
						if (rowCandidate.size() < 5) {
							row = rowCandidate;
							break;
						}
					}
				} else {
					row = rows.get(selectComponent.row());
				}

				if (row != null) {
					row.add(selectComponent.toJda());
				}


			}
		}
		
		if (!buttonComponents.isEmpty()) {
			register = true;

			for (ButtonComponent buttonComponent : buttonComponents) {

				List<ActionComponent> row = null;
				if (buttonComponent.row() < 0) {
					// Search for first available row
					for (List<ActionComponent> rowCandidate : rows) {
						if (rowCandidate.size() < 5) {
							row = rowCandidate;
							break;
						}
					}
				} else {
					row = rows.get(buttonComponent.row());
				}

				if (row != null) {
					row.add(buttonComponent.toJda());
				}
			}
		}

		if (register) {
			registerComponents();
		}

		return rows;
	}

	public @NotNull List<ActionRow> getActionRows(long userId) {
		List<List<ActionComponent>> rows = new ArrayList<>();

		for (int i = 0; i < 5; i++) rows.add(new ArrayList<>());

		if (!buttonComponents.isEmpty()) {
			for (ButtonComponent buttonComponent : buttonComponents) {
				List<ActionComponent> row;
				if (buttonComponent.row() < 0) {
					row = null;
					for (List<ActionComponent> rowCandidate : rows) {
						if (rowCandidate.size() < 5) {
							row = rowCandidate;
							break;
						}
					}
				} else {
					row = rows.get(buttonComponent.row());
				}

				if (row != null) {
					row.add(buttonComponent.toJda());
				}
			}
			registerComponents();
		}

		return rows.stream()
				.filter(r -> !r.isEmpty())
				.map(ActionRow::of)
				.toList();
	}

	private WebhookMessageCreateAction<Message> addActionRows(@NotNull WebhookMessageCreateAction<Message> action) {
		for (List<ActionComponent> row : getActionMatrix()) {
			if (row.isEmpty()) {
				continue;
			}
			action = action.addActionRow(row);
		}
		return action;
	}

	public @NotNull WebhookMessageEditAction<Message> editOriginal(
			@NotNull String content,
			@NotNull InteractionHook hook
	) {
		return hook.editOriginal(content);
	}

	public @NotNull WebhookMessageCreateAction<Message> sendMessage(
			@NotNull String content,
			@NotNull InteractionHook hook
	) {
		return addActionRows(hook.sendMessage(content));
	}

	public @NotNull WebhookMessageCreateAction<Message> sendMessage(
			@NotNull InteractionHook hook
	) {
		return addActionRows(hook.sendMessage(""));
	}

	public @NotNull WebhookMessageCreateAction<Message> sendEmbed(
			@NotNull String content,
			@NotNull net.dv8tion.jda.api.entities.MessageEmbed embed,
			@NotNull InteractionHook hook
	) {
		return addActionRows(hook.sendMessage(content).addEmbeds(embed));
	}

	public @NotNull WebhookMessageCreateAction<Message> sendEmbed(
			@NotNull net.dv8tion.jda.api.entities.MessageEmbed embed,
			@NotNull InteractionHook hook
	) {
		return addActionRows(hook.sendMessageEmbeds(embed));
	}

	public @NotNull WebhookMessageCreateAction<Message> sendEmbeds(
			@NotNull String content,
			@NotNull InteractionHook hook,
			@NotNull net.dv8tion.jda.api.entities.MessageEmbed... embeds
	) {
		return addActionRows(hook.sendMessage(content).addEmbeds(embeds));
	}

	public @NotNull WebhookMessageCreateAction<Message> sendEmbeds(
			@NotNull InteractionHook hook,
			@NotNull net.dv8tion.jda.api.entities.MessageEmbed... embeds
	) {
		return addActionRows(hook.sendMessageEmbeds(java.util.Arrays.asList(embeds)));
	}

	public @NotNull WebhookMessageCreateAction<Message> sendFile(
			@NotNull String content,
			@NotNull java.io.File file,
			@NotNull InteractionHook hook
	) {
		return addActionRows(hook.sendMessage(content).addFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(file)));
	}

	public @NotNull WebhookMessageCreateAction<Message> sendFile(
			@NotNull String content,
			@NotNull java.io.File file,
			@NotNull String fileName,
			@NotNull InteractionHook hook
	) {
		return addActionRows(hook.sendMessage(content).addFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(file, fileName)));
	}

	public @NotNull WebhookMessageCreateAction<Message> sendFile(
			@NotNull String content,
			byte[] data,
			@NotNull String fileName,
			@NotNull InteractionHook hook
	) {
		return addActionRows(hook.sendMessage(content).addFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(data, fileName)));
	}

	public @NotNull WebhookMessageCreateAction<Message> sendFile(
			@NotNull String content,
			@NotNull FileUpload file,
			@NotNull InteractionHook hook
	) {
		return addActionRows(hook.sendMessage(content).addFiles(file));
	}

	public @NotNull WebhookMessageCreateAction<Message> sendFiles(
			@NotNull String content,
			@NotNull InteractionHook hook,
			@NotNull FileUpload... files
	) {
		return addActionRows(hook.sendMessage(content).addFiles(files));
	}

	private void registerComponents() {
		for (ButtonComponent buttonComponent : buttonComponents) {
			MessageComponentManager.registerComponent(buttonComponent.id(), buttonComponent);
		}
		for (SelectComponent selectComponent : selectComponents) {
			MessageComponentManager.registerComponent(selectComponent.id(), selectComponent);
		}
	}

	public @NotNull MessageInstance button(
			@NotNull String id,
			int row,
			@NotNull String label,
			@NotNull ButtonStyle buttonStyle,
			@Nullable Emoji emoji,
			boolean disabled,
			boolean authorOnly,
			@NotNull String... args
	) {
		if (id.length() > 60) {
			throw new IllegalArgumentException("Button id cant be longer than 60 characters!");
		}
		int argHash = MessageComponentManager.storeButtonArgs(userId, id, args);

		String finalButtonId = ButtonComponent.PREFIX + ":" + userId + ":" + id;

		buttonComponents.add(new ButtonComponent(finalButtonId, row, label, buttonStyle, disabled, emoji, authorOnly, argHash));
		return this;
	}

	public @NotNull MessageInstance buttonPrimary(
			@NotNull String id,
			int row,
			@NotNull String label,
			boolean authorOnly,
			@NotNull String... args
	) {
		return button(id, row, label, ButtonStyle.PRIMARY, null, false, authorOnly, args);
	}

	public @NotNull MessageInstance buttonPrimary(
			@NotNull String id,
			int row,
			@NotNull String label,
			@Nullable Emoji emoji,
			boolean authorOnly,
			@NotNull String... args
	) {
		return button(id, row, label, ButtonStyle.PRIMARY, emoji, false, authorOnly, args);
	}

	public @NotNull MessageInstance buttonSecondary(
			@NotNull String id,
			int row,
			@NotNull String label,
			boolean authorOnly,
			@NotNull String... args
	) {
		return button(id, row, label, ButtonStyle.SECONDARY, null, false, authorOnly, args);
	}

	public @NotNull MessageInstance buttonSecondary(
			@NotNull String id,
			int row,
			@NotNull String label,
			@Nullable Emoji emoji,
			boolean authorOnly,
			@NotNull String... args
	) {
		return button(id, row, label, ButtonStyle.SECONDARY, emoji, false, authorOnly, args);
	}

	public @NotNull MessageInstance buttonSuccess(
			@NotNull String id,
			int row,
			@NotNull String label,
			boolean authorOnly,
			@NotNull String... args
	) {
		return button(id, row, label, ButtonStyle.SUCCESS, null, false, authorOnly, args);
	}

	public @NotNull MessageInstance buttonSuccess(
			@NotNull String id,
			int row,
			@NotNull String label,
			@Nullable Emoji emoji,
			boolean authorOnly,
			@NotNull String... args
	) {
		return button(id, row, label, ButtonStyle.SUCCESS, emoji, false, authorOnly, args);
	}

	public @NotNull MessageInstance buttonDanger(
			@NotNull String id,
			int row,
			@NotNull String label,
			boolean authorOnly,
			@NotNull String... args
	) {
		return button(id, row, label, ButtonStyle.DANGER, null, false, authorOnly, args);
	}

	public @NotNull MessageInstance buttonDanger(
			@NotNull String id,
			int row,
			@NotNull String label,
			@Nullable Emoji emoji,
			boolean authorOnly,
			@NotNull String... args
	) {
		return button(id, row, label, ButtonStyle.DANGER, emoji, false, authorOnly, args);
	}

	public @NotNull MessageInstance buttonLink(
			@NotNull String url,
			int row,
			@NotNull String label
	) {
		return buttonLink(url, row, label, null, false);
	}

	public @NotNull MessageInstance buttonLink(
			@NotNull String url,
			int row,
			@NotNull String label,
			@Nullable Emoji emoji,
			boolean disabled
	) {
		if (url.length() > 512) {
			throw new IllegalArgumentException("Button URL cant be longer than 512 characters!");
		}
		buttonComponents.add(new ButtonComponent(url, row, label, ButtonStyle.LINK, disabled, emoji, false, 0));
		return this;
	}

	public @NotNull MessageInstance stringSelect(
		@NotNull String id,
		int row,
		@NotNull String placeholder,
		int minRange,
		int maxRange,
		@NotNull List<SelectOption> optionList,
		@NotNull List<String> defaultValueList,
		boolean disabled,
		boolean authorOnly,
		@NotNull String... args
	) {
		if (id.length() > 60) {
			throw new IllegalArgumentException("Select id cant be longer than 60 characters!");
		}

		int argHash = MessageComponentManager.storeSelectArgs(userId, id, args);

		String finalSelectId = SelectComponent.PREFIX + ":" + userId + ":" + id;

		selectComponents.add(new SelectComponent(finalSelectId, row, placeholder, minRange, maxRange, optionList, defaultValueList, disabled, authorOnly, argHash));
		return this;
	}

	public @NotNull SelectBuilder stringSelect(@NotNull String id, int row, @NotNull String placeholder, @NotNull String... args) {
		return new SelectBuilder(this, id, row, placeholder, args);
	}
}
