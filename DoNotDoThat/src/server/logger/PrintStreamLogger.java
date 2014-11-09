package server.logger;

import java.io.PrintStream;

/**
 * A logging target that will write all messages to the specified PrintStream.
 *
 * @author skeggsc
 */
public final class PrintStreamLogger implements LoggingTarget {

	/**
	 * The PrintStream to write the logs to.
	 */
	private final PrintStream str;

	/**
	 * Create a new PrintStreamLogger to log to the specific output.
	 *
	 * @param out
	 *            the PrintStream to log to.
	 */
	public PrintStreamLogger(PrintStream out) {
		if (out == null) {
			throw new NullPointerException();
		}
		this.str = out;
	}

	public synchronized void log(LogLevel level, String message, Throwable thr) {
		if (thr != null) {
			str.println("LOG{" + level.message + "} " + message);
			thr.printStackTrace(str);
		} else {
			str.println("LOG[" + level.message + "] " + message);
		}
	}

	public synchronized void log(LogLevel level, String message, String extended) {
		str.println("LOG[" + level.message + "] " + message);
		if (extended != null && !extended.isEmpty()) {
			str.println(extended);
		}
	}
}
