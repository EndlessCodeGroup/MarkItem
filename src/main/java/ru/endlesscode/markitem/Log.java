package ru.endlesscode.markitem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

class Log {

    @Nullable
    static private Logger logger;

    private Log() {
        // should not be instantiated
    }

    static void init(@NotNull Logger logger) {
        Log.logger = logger;
    }

    static void i(String message, Object... params) {
        log(Level.INFO, message, params);
    }

    static void w(String message, Object... params) {
        log(Level.WARNING, message, params);
    }

    private static void log(Level level, String message, Object[] params) {
        if (logger == null) return;
        logger.log(level, message, params);
    }
}
