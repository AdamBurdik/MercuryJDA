package me.adamix.mercury.jda.components.modal;

import me.adamix.mercury.jda.components.message.button.ButtonComponent;
import me.adamix.mercury.jda.components.result.Result;
import me.adamix.mercury.jda.context.Context;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ModalComponentManager {
	private static final Map<@NotNull String, @NotNull ModalHandler> modalHandlerMap = new HashMap<>();
	private static final Map<@NotNull String, @NotNull ModalComponent> modalComponentMap = new HashMap<>();

	private static final Map<ArgKey, String[]> argMap = new ConcurrentHashMap<>();

	record ArgKey(long userId, int argsHash) {}

	public static @NotNull Result handle(@NotNull ModalInteractionEvent event) {
		if (event.isAcknowledged()) return Result.INVALID;
		String fullModalId = event.getModalId();

		String[] parts = fullModalId.split(":");
		if (!parts[0].equals(ModalComponent.PREFIX)) return Result.INVALID;

		long userId = Long.parseLong(parts[1]);
		// Raw modal id (<modal_id>)
		String modalId = parts[2];
		int argHash = Integer.parseInt(parts[3]);

		// Modal id without arg hash (mercury:<user_id>:<modal_id>)
		String prefixedModalId = ModalComponent.PREFIX + ":" + userId + ":" + modalId;

		ModalHandler handler = modalHandlerMap.get(modalId);
		ModalComponent component = modalComponentMap.get(prefixedModalId);
		if (handler == null || component == null) return Result.EXPIRED;

		ArgKey argKey = new ArgKey(userId, argHash);
		String[] args = argMap.get(argKey);
		if (args == null) args = new String[]{};

		return handler.accept(event, new Context<>(component, args));
	}

	public static void registerHandler(
			@NotNull String id,
			@NotNull ModalHandler handler
	) {
		modalHandlerMap.put(id, handler);
	}

	public static void registerComponent(
			@NotNull String id,
			@NotNull ModalComponent component
	) {
		modalComponentMap.put(id, component);
	}

	public static int storeArgs(
			long userId,
			@NotNull String[] args
	) {
		if (args.length == 0) return 0;

		int argsHash = Objects.hash(Arrays.hashCode(args), System.nanoTime());
		argMap.put(new ArgKey(userId, argsHash), args);
		return argsHash;
	}
}
