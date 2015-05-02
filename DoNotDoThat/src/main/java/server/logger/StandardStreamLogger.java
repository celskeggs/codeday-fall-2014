package server.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A logging target that will write all messages to the standard error. This is
 * the default logger.
 *
 * @author skeggsc
 */
final class StandardStreamLogger implements LoggingTarget {

	private static final DateFormat fmt = new SimpleDateFormat("(yyyy.MM.dd HH:mm:ss.SSS)");

	public synchronized void log(LogLevel level, String message, Throwable thr) {
		System.err.println(fmt.format(new Date()) + level.message + ": " + message);
		if (thr != null) {
			thr.printStackTrace(System.err);
		}
	}

	public synchronized void log(LogLevel level, String message, String extended) {
		System.err.println(fmt.format(new Date()) + level.message + ": " + message);
		if (extended != null && !extended.isEmpty()) {
			System.err.println(extended);
		}
	}
}
