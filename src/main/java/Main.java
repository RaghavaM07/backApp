import daemon.Config.CoreConfig;
import daemon.ConfigLoader.CoreConfigLoader;
import daemon.Constants;
import daemon.Daemon;
import daemon.Utils;
import logger.LoggerUtil;

import java.io.File;
import java.util.Objects;
import java.util.logging.Level;


public class Main {

    public static void main(String[] args) {

        // first and foremost, read core config
        CoreConfig coreConfig = new CoreConfig();
        try {
            CoreConfigLoader coreLoader = new CoreConfigLoader(Constants.DEFAULT_CORE_CONFIG_FILE);
            coreConfig = coreLoader.load();
            new File(coreConfig.getLogging().getLogFileLocation()).createNewFile();
        } catch (Exception e) {
            Utils.errorHandler(e);
        }

        LoggerUtil.init(
                resolveLogLvl(coreConfig.getLogging().getLogLevel()),
                Objects.equals(System.getenv("ENV"), "dev") ? "" : coreConfig.getLogging().getLogFileLocation());

        new Daemon(coreConfig).run();
    }

    private static Level resolveLogLvl(String fromFile) {
        return switch (fromFile) {
            case "ALL" -> Level.ALL;
            case "FINE" -> Level.FINEST;
            case "WARNING" -> Level.WARNING;
            case "SEVERE" -> Level.SEVERE;
            default -> Level.INFO;
        };
    }
}
