package server.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

/**
 * A logging tool that stores logging message in a file on the current computer
 * or robot.
 *
 * @author skeggsc
 */
public class FileLogger implements LoggingTarget {

    /**
     * Register a new FileLogger writing to a unique file with the logging
     * manager.
     */
    public static void register() {
        try {
            int i = 0;
            while (true) {
            	File f = new File("log-" + i);
            	if (!f.exists()) {
                    Logger.addTarget(new FileLogger(f));
            		break;
            	}
                i++;
            }
        } catch (IOException ex) {
            Logger.warning("Could not set up File logging!", ex);
        }
    }

    /**
     * The PrintStream to log outputs to.
     */
    private final PrintStream pstream;
    /**
     * The time at which this logger was started.
     */
    private final long start;

    /**
     * Create a new FileLogger writing to the specified output file.
     *
     * @param fname The filename to write to
     * @throws IOException If an IO Exception occurs.
     */
    public FileLogger(File fname) throws IOException {
        this(new FileOutputStream(fname));
    }

    /**
     * Create a new FileLogger writing to the specified output stream.
     *
     * @param out The output stream to write to.
     */
    public FileLogger(OutputStream out) {
        this(out instanceof PrintStream ? (PrintStream) out : new PrintStream(out));
    }

    /**
     * Create a new FileLogger writing to the specified PrintStream.
     *
     * @param pstream The stream to write to.
     */
    public FileLogger(final PrintStream pstream) {
        this.pstream = pstream;
        start = System.currentTimeMillis();
        pstream.println("Logging began at " + new Date(start) + " [" + start + "]");
    }

    public synchronized void log(LogLevel level, String message, Throwable throwable) {
        pstream.println("[" + (System.currentTimeMillis() - start) + " " + level + "] " + message);
        if (throwable != null) {
        	throwable.printStackTrace(pstream);
        }
        pstream.flush();
    }

    public synchronized void log(LogLevel level, String message, String extended) {
        pstream.println("[" + (System.currentTimeMillis() - start) + " " + level + "] " + message);
        if (extended != null) {
            int i = extended.length();
            while (i != 0 && extended.charAt(i - 1) <= 32) {
                i -= 1;
            }
            if (i != 0) {
                pstream.println(extended);
            }
        }
        pstream.flush();
    }
}
