package server.logger;

/**
 * A logging target that will write all messages to the standard error. This is
 * the default logger.
 *
 * @author skeggsc
 */
final class StandardStreamLogger implements LoggingTarget {

    public synchronized void log(LogLevel level, String message, Throwable thr) {
        if (thr != null) {
            System.err.println("LOG{" + level.message + "} " + message);
            thr.printStackTrace(System.err);
        } else {
            System.err.println("LOG[" + level.message + "] " + message);
        }
    }

    public synchronized void log(LogLevel level, String message, String extended) {
        System.err.println("LOG[" + level.message + "] " + message);
        if (extended != null && !extended.isEmpty()) {
            System.err.println(extended);
        }
    }
}
