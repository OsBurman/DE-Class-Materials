import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * SINGLETON PATTERN — Logger
 *
 * Only ONE Logger instance should ever exist in the application.
 *
 * TODO 1: Declare a private static volatile Logger instance field.
 * TODO 2: Make the constructor private.
 * TODO 3: Implement getInstance() with double-checked locking:
 * if (instance == null) {
 * synchronized (Logger.class) {
 * if (instance == null) { instance = new Logger(); }
 * }
 * }
 * return instance;
 * TODO 4: Implement log(String message) — print "[LOG HH:mm:ss] message"
 */
public class Logger {

    // TODO 1: private static volatile Logger instance;

    // TODO 2: private constructor
    Logger() {
    } // ← change to private

    // TODO 3: public static Logger getInstance() { ... }

    // TODO 4: public void log(String message) { ... }
}
