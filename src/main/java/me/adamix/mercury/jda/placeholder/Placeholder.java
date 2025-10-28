package me.adamix.mercury.jda.placeholder;

import me.adamix.mercury.jda.exception.ParsingException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Placeholder {

	@FunctionalInterface
	interface Data<T> extends Placeholder {
		@NotNull String get(@NotNull ArgumentQueue args, @NotNull T value) throws ParsingException;
	}

	@FunctionalInterface
	interface Global extends Placeholder {
		@NotNull String get(@NotNull ArgumentQueue args) throws ParsingException;
	}

	@FunctionalInterface
	interface Redirect<T> extends Placeholder {
		@NotNull List<Object> expand(@NotNull T source);
	}
}