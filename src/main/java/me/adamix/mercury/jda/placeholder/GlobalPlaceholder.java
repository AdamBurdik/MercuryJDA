package me.adamix.mercury.jda.placeholder;

import me.adamix.mercury.jda.exception.ParsingException;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface GlobalPlaceholder {
    String get(@NotNull ArgumentQueue args) throws ParsingException;
}