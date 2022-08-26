package ru.endlesscode.markitem.util;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class Strings {

    private Strings() {
        // Should not be instantiated
    }

    /**
     * Converts an item ID pattern to Regex.
     */
    static public @NotNull Pattern parseItemIdPattern(@NotNull String pattern) {
        String regex = Pattern.quote(pattern.trim())
                .replace("*", "\\E.*\\Q")
                .replace("?", "\\E.\\Q")
                .replace("\\Q\\E", "");

        if (!regex.contains(":")) regex = "(.+:)?" + regex;

        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    /**
     * Returns colorized string.
     */
    static public @NotNull String colorize(@NotNull String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
