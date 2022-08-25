package ru.endlesscode.markitem.util;

import org.bukkit.ChatColor;
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

    /**
     * Returns colorized string.
     */
    static public @NotNull String colorize(@NotNull String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
