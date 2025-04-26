package logger;

import daemon.Utils;

import java.io.IOException;
import java.util.logging.*;

public class LoggerUtil {
    private static boolean initialized = false;

    // Static initializer to set up global logging configurations
    public static void init(Level logLevel, String logLocation) {
        if (initialized) return;

        // Reset any existing configurations
        LogManager.getLogManager().reset();
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(logLevel);

        Handler handler = null;
        if (logLocation != null && !logLocation.isEmpty()) {
            try {
                handler = new FileHandler(logLocation, true);
            } catch (IOException e) {
                Utils.errorHandler(e);
                handler = new ConsoleHandler();
            }
        } else {
            handler = new ConsoleHandler();
        }

        handler.setLevel(logLevel);
        handler.setFormatter(new CustomFormatter());
        rootLogger.addHandler(handler);

        initialized = true;
    }

    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getCanonicalName());
    }
}
