package server.logger;

import java.util.ArrayList;

/**
 * A class containing easy global methods for logging, as well as holding the
 * default logger field.
 *
 * @author skeggsc
 */
public class Logger {

    /**
     * The logging targets to write logs to.
     */
    public static final ArrayList<LoggingTarget> targets = new ArrayList<LoggingTarget>();

    static {
        targets.add(new StandardStreamLogger());
    }

    /**
     * Add the specified target to the list of targets.
     *
     * @param lt The target to add.
     */
    public static synchronized void addTarget(LoggingTarget lt) {
        targets.add(lt);
    }

    /**
     * Remove the specified target from the list of targets.
     *
     * @param lt The target to remove.
     */
    public static synchronized void removeTarget(LoggingTarget lt) {
        targets.remove(lt);
    }

    /**
     * Log a given message and throwable at the given log level.
     *
     * @param level the level to log at.
     * @param message the message to log.
     * @param thr the Throwable to log
     */
    public static void log(LogLevel level, String message, Throwable thr) {
        if (level == null || message == null) {
            throw new NullPointerException();
        }
        for (LoggingTarget lt : targets) {
            lt.log(level, message, thr);
        }
    }

    /**
     * Log a given message and extended message at the given log level.
     *
     * @param level the level to log at.
     * @param message the message to log.
     * @param extended the extended message to log
     */
    public static void logExt(LogLevel level, String message, String extended) {
        if (level == null || message == null) {
            throw new NullPointerException();
        }
        for (LoggingTarget lt : targets) {
            lt.log(level, message, extended);
        }
    }

    /**
     * Log a given message at the given log level.
     *
     * @param level the level to log at.
     * @param message the message to log.
     */
    public static void log(LogLevel level, String message) {
        log(level, message, null);
    }

    /**
     * Log the given message at SEVERE level.
     *
     * @param message the message to log.
     */
    public static void severe(String message) {
        log(LogLevel.SEVERE, message);
    }

    /**
     * Log the given message at WARNING level.
     *
     * @param message the message to log.
     */
    public static void warning(String message) {
        log(LogLevel.WARNING, message);
    }

    /**
     * Log the given message at INFO level.
     *
     * @param message the message to log.
     */
    public static void info(String message) {
        log(LogLevel.INFO, message);
    }

    /**
     * Log the given message at CONFIG level.
     *
     * @param message the message to log.
     */
    public static void config(String message) {
        log(LogLevel.CONFIG, message);
    }

    /**
     * Log the given message at FINE level.
     *
     * @param message the message to log.
     */
    public static void fine(String message) {
        log(LogLevel.FINE, message);
    }

    /**
     * Log the given message at FINER level.
     *
     * @param message the message to log.
     */
    public static void finer(String message) {
        log(LogLevel.FINER, message);
    }

    /**
     * Log the given message at FINEST level.
     *
     * @param message the message to log.
     */
    public static void finest(String message) {
        log(LogLevel.FINEST, message);
    }

    /**
     * Log the given message and exception at SEVERE level.
     *
     * @param message the message to log.
     * @param thr The exception to include in the log.
     */
    public static void severe(String message, Throwable thr) {
        log(LogLevel.SEVERE, message, thr);
    }

    /**
     * Log the given message and exception at WARNING level.
     *
     * @param message the message to log.
     * @param thr The exception to include in the log.
     */
    public static void warning(String message, Throwable thr) {
        log(LogLevel.WARNING, message, thr);
    }

    /**
     * Log the given message and exception at INFO level.
     *
     * @param message the message to log.
     * @param thr The exception to include in the log.
     */
    public static void info(String message, Throwable thr) {
        log(LogLevel.INFO, message, thr);
    }

    /**
     * Log the given message and exception at CONFIG level.
     *
     * @param message the message to log.
     * @param thr The exception to include in the log.
     */
    public static void config(String message, Throwable thr) {
        log(LogLevel.CONFIG, message, thr);
    }

    /**
     * Log the given message and exception at FINE level.
     *
     * @param message the message to log.
     * @param thr The exception to include in the log.
     */
    public static void fine(String message, Throwable thr) {
        log(LogLevel.FINE, message, thr);
    }

    /**
     * Log the given message and exception at FINER level.
     *
     * @param message the message to log.
     * @param thr The exception to include in the log.
     */
    public static void finer(String message, Throwable thr) {
        log(LogLevel.FINER, message, thr);
    }

    /**
     * Log the given message and exception at FINEST level.
     *
     * @param message the message to log.
     * @param thr The exception to include in the log.
     */
    public static void finest(String message, Throwable thr) {
        log(LogLevel.FINEST, message, thr);
    }

    private Logger() {
    }
}
