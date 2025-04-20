package daemon;

public class Constants {
    public static String DEFAULT_CORE_CONFIG_FILE = System.getenv().getOrDefault("BACKUP_DAEMON_CONFIG", System.getProperty("user.home") + "/.config/backApp/core-config.json");
    public static String DEFAULT_BACKUP_DIR = System.getProperty("user.home") + "/backApp-Backups/backups/";
    public static String DEFAULT_LOG_FILE = System.getProperty("user.home") + "/backApp-Backups/backApp.log";
}
