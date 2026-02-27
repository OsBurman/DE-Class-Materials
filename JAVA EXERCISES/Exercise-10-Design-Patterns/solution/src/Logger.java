import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** SINGLETON — Logger (Solution) */
public class Logger {
    private static volatile Logger instance;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private Logger() {}

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) instance = new Logger();
            }
        }
        return instance;
    }

    public void log(String message) {
        System.out.println("[LOG " + LocalTime.now().format(FMT) + "] " + message);
    }
}
