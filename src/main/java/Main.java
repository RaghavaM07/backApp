import daemon.Config.CoreConfig;
import daemon.ConfigLoader.CoreConfigLoader;
import daemon.Constants;
import daemon.Daemon;
import daemon.Utils;


public class Main {

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s [%1$tc]%n");

        // first and foremost, read core config
        CoreConfig coreConfig = new CoreConfig();
        try {
            CoreConfigLoader coreLoader = new CoreConfigLoader(Constants.DEFAULT_CORE_CONFIG_FILE);
            coreConfig = coreLoader.load();
        } catch (Exception e) {
            Utils.errorHandler(e);
        }

        // TODO: set universal log level from core config

        new Daemon(coreConfig).run();
    }
}
