package ru.endlesscode.markitem.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

public class Lists {

    private Lists() {
        // Should not be instantiated
    }

    public static boolean noneMatch(List<Pattern> patterns, @NotNull String value) {
        return !anyMatch(patterns, value);
    }

    public static boolean anyMatch(List<Pattern> patterns, @NotNull String value) {
        return patterns.stream().anyMatch(pattern -> pattern.matcher(value).matches());
    }
}
