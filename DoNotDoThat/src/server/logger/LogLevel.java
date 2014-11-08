package server.logger;

import java.io.Serializable;

/**
 * Represents a Logging level. This represents how important/severe a logging
 * message is. The levels are, in order of descending severity: severe, warning,
 * info, config, fine, finer, finest.
 *
 * @author skeggsc
 */
public class LogLevel implements Serializable {

    private static final long serialVersionUID = 6646883245419060561L;
    /**
     * A severe error. This usually means that something major didn't work, or
     * an impossible condition occurred.
     */
    public static final LogLevel SEVERE = new LogLevel(9, "SEVERE");
    /**
     * A warning. This usually means that something bad happened, but most
     * things should probably still work.
     */
    public static final LogLevel WARNING = new LogLevel(6, "WARNING");
    /**
     * A piece of info. This usually means something happened that the user
     * might want to know.
     */
    public static final LogLevel INFO = new LogLevel(3, "INFO");
    /**
     * A piece of configuration information. This usually means something that
     * isn't really important, but is something triggered by configuration
     * instead of normal operation.
     */
    public static final LogLevel CONFIG = new LogLevel(0, "CONFIG");
    /**
     * A top-level debugging message. This can be caused by anything, but
     * probably shouldn't be logged particularly often.
     */
    public static final LogLevel FINE = new LogLevel(-3, "FINE");
    /**
     * A mid-level debugging message. This can be caused by anything, and can be
     * logged relatively often.
     */
    public static final LogLevel FINER = new LogLevel(-6, "FINER");
    /**
     * A low-level debugging message. This can be caused by anything, and might
     * be called many times per second.
     */
    public static final LogLevel FINEST = new LogLevel(-9, "FINEST");

    private static final LogLevel[] levels = new LogLevel[] { FINEST, FINER, FINE, CONFIG, INFO, WARNING, SEVERE };

    /**
     * Get a LogLevel from its ID level. If it doesn't exist, a RuntimeException
     * is thrown. Should probably only be called on the result of toByte.
     *
     * @param id the ID of the LogLevel.
     * @return the LogLevel with this ID.
     * @see #id
     * @see #toByte(ccre.log.LogLevel)
     */
    public static LogLevel fromByte(byte id) {
        if ((id + 9) % 3 != 0 || id < -9 || id > 9) {
            throw new RuntimeException("Invalid LogLevel ID: " + id);
        }
        return levels[(id + 9) / 3];
    }

    /**
     * Return a byte representing this logging level - that is, its ID. Used in
     * fromByte.
     *
     * @param level the LogLevel to serialize.
     * @return the byte version of the LogLevel.
     * @see #id
     * @see #fromByte(byte)
     */
    public static byte toByte(LogLevel level) {
        return level.id;
    }

    /**
     * The ID of the LogLevel. The higher, the more severe. SEVERE is 9, FINEST
     * is -9, for example.
     */
    public final byte id;
    /**
     * The long-form message representing this level.
     */
    public final String message;

    private LogLevel(int id, String msg) {
        this.id = (byte) id;
        if (id != this.id) {
            throw new IllegalArgumentException();
        }
        message = msg;
    }

    /**
     * Check if this logging level is at least as important/severe as the other
     * logging level.
     *
     * @param other the logging level to compare to.
     * @return if this is at least as important.
     */
    public boolean atLeastAsImportant(LogLevel other) {
        return id >= other.id;
    }

    /**
     * Convert this LogLevel to a string. Returns the message.
     *
     * @return the message.
     */
    @Override
    public String toString() {
        return message;
    }

    private Object readResolve() {
        return fromByte(id);
    }
}
