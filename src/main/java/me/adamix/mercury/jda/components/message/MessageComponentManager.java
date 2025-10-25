package me.adamix.mercury.jda.components.message;

import me.adamix.mercury.jda.components.message.button.ButtonComponent;
import me.adamix.mercury.jda.components.message.button.handler.ButtonHandler;
import me.adamix.mercury.jda.components.message.select.SelectComponent;
import me.adamix.mercury.jda.components.message.select.handler.SelectHandler;
import me.adamix.mercury.jda.components.result.Result;
import me.adamix.mercury.jda.context.Context;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MessageComponentManager {
	private static final Map<@NotNull String, @NotNull ButtonHandler> buttonHandlerMap = new HashMap<>();
	private static final Map<@NotNull String, @NotNull ButtonComponent> buttonComponentMap = new HashMap<>();

	private static final Map<@NotNull String, @NotNull SelectHandler> selectHandlerMap = new HashMap<>();
	private static final Map<@NotNull String, @NotNull SelectComponent> selectComponentMap = new HashMap<>();

	private static final Map<ArgKey, String[]> argMap = new ConcurrentHashMap<>();

	public static @NotNull Result handleButton(@NotNull ButtonInteractionEvent event) {
		if (event.isAcknowledged()) return Result.INVALID;
		// Button id with everything (mercury:<user_id>:<button_id>:<arg_hash>)
		String fullButtonId = event.getButton().getId();

		if (fullButtonId == null) return Result.INVALID;

		String[] parts = fullButtonId.split(":");
		if (!parts[0].equals(ButtonComponent.PREFIX)) return Result.INVALID;

		long userId = Long.parseLong(parts[1]);
		// Raw button id (<button_id>)
		String buttonId = parts[2];
		int argHash = Integer.parseInt(parts[3]);

		// Button id without arg hash (mercury:<user_id>:<button_id>)
		String prefixedButtonId = ButtonComponent.PREFIX + ":" + userId + ":" + buttonId;

		ButtonHandler buttonHandler = buttonHandlerMap.get(buttonId);
		ButtonComponent buttonComponent = buttonComponentMap.get(prefixedButtonId);
		if (buttonHandler == null || buttonComponent == null) return Result.EXPIRED;

		if (buttonComponent.authorOnly()) {
			if (event.getUser().getIdLong() != userId) {
				return Result.DIFFERENT_AUTHOR;
			}
		}

		ArgKey argKey = new ArgKey(userId, "button-" + buttonId, argHash);
		String[] args = argMap.get(argKey);
		if (args == null) args = new String[]{};

		return buttonHandler.accept(event, new Context<>(buttonComponent, args));
	}

	public static @NotNull Result handleSelect(@NotNull StringSelectInteractionEvent event) {
		if (event.isAcknowledged()) return Result.INVALID;
		// Button id with everything (mercury:<user_id>:<button_id>:<arg_hash>)
		String fullSelectId = event.getSelectMenu().getId();

		if (fullSelectId == null) return Result.INVALID;

		String[] parts = fullSelectId.split(":");
		if (!parts[0].equals(SelectComponent.PREFIX)) return Result.INVALID;

		long userId = Long.parseLong(parts[1]);
		// Raw select id (<select_id>)
		String selectId = parts[2];
		int argHash = Integer.parseInt(parts[3]);

		// Select id without arg hash (mercury:<user_id>:<select_id>)
		String prefixedSelectId = SelectComponent.PREFIX + ":" + userId + ":" + selectId;

		SelectHandler selectHandler = selectHandlerMap.get(selectId);
		SelectComponent selectComponent = selectComponentMap.get(prefixedSelectId);
		if (selectHandler == null || selectComponent == null) return Result.EXPIRED;

		if (selectComponent.authorOnly()) {
			if (event.getUser().getIdLong() != userId) {
				return Result.DIFFERENT_AUTHOR;
			}
		}

		ArgKey argKey = new ArgKey(userId, "select-" + selectId, argHash);
		String[] args = argMap.get(argKey);
		if (args == null) args = new String[]{};

		return selectHandler.accept(event, new Context<>(selectComponent, args));
	}

	public static void registerHandler(
			@NotNull String id,
			@NotNull ButtonHandler handler
	) {
		buttonHandlerMap.put(id, handler);
	}

	public static void registerComponent(
			@NotNull String id,
			@NotNull ButtonComponent component
	) {
		buttonComponentMap.put(id, component);
	}


	public static void registerHandler(
			@NotNull String id,
			@NotNull SelectHandler handler
	) {
		selectHandlerMap.put(id, handler);
	}

	public static void registerComponent(
			@NotNull String id,
			@NotNull SelectComponent component
	) {
		selectComponentMap.put(id, component);
	}



	private static int storeArgs(
			long userId,
			@NotNull String id,
			@NotNull String[] args
	) {
		if (args.length == 0) return 0;

		int argsHash = Objects.hash(Arrays.hashCode(args), System.nanoTime());
		argMap.put(new ArgKey(userId, id, argsHash), args);
		return argsHash;
	}


	public static int storeButtonArgs(
			long userId,
			@NotNull String id,
			@NotNull String[] args
	) {
		return storeArgs(userId, "button-" + id, args);
	}

	public static int storeSelectArgs(
			long userId,
			@NotNull String id,
			@NotNull String[] args
	) {
		return storeArgs(userId, "select-" + id, args);
	}

	record ArgKey(long userId, @NotNull String id, int argsHash) {}
}
