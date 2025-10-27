package me.adamix.mercury.jda.placeholder;

import me.adamix.mercury.jda.exception.ParsingException;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Placeholder<T> {
	String get(@NotNull ArgumentQueue args, @NotNull T value) throws ParsingException;
}