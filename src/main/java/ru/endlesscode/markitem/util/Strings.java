package ru.endlesscode.markitem.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class Strings {

    private Strings() {
        // Should not be instantiated
    }

    /**
     * Converts a simple pattern String to a Regex.
     */
    static public @NotNull Pattern parseSimplePattern(@NotNull String pattern) {
        String regex = pattern
                .replace(".", "\\.")
                .replace("*", ".*")
                .replace("?", ".");
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }
}
