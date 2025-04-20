package daemon.ConfigLoader;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import daemon.Config.CoreConfig;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class CoreConfigLoaderTest {

    @Test
    void loadsGoodFile() {
        CoreConfig coreConfig = new CoreConfig();
        try {
            CoreConfigLoader coreConfigLoader = new CoreConfigLoader("/core-config/core-config-1.json");
            coreConfig = coreConfigLoader.load();
        } catch (Exception e) {
            System.out.println("Caught exception: "+ e.getClass() + " - " + e.getMessage());
        }

        assertEquals("/home/raghava/backups/", coreConfig.getBackupConfigFileLocation());
        assertFalse(coreConfig.isUseSysTempAsFallback());
        assertEquals("INFO", coreConfig.getLogInfo().getLogLevel());
        assertEquals("/var/logs/backApp.log", coreConfig.getLogInfo().getLogFileLocation());
    }

    @Test
    void loadsDefaultValues() {
        CoreConfig coreConfig = new CoreConfig();
        try {
            CoreConfigLoader coreConfigLoader = new CoreConfigLoader("/core-config/core-config-2.json");
            coreConfig = coreConfigLoader.load();
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
        }

        assertEquals(System.getProperty("user.home") + "/backApp-Backups/backups/", coreConfig.getBackupConfigFileLocation());
        assertTrue(coreConfig.isUseSysTempAsFallback());
        assertEquals("TRACE", coreConfig.getLogInfo().getLogLevel());
        assertEquals(System.getProperty("user.home") + "/backApp-Backups/backApp.log", coreConfig.getLogInfo().getLogFileLocation());
    }

    @Test
    void throwsException() {
        AtomicReference<CoreConfig> coreConfig = new AtomicReference<>(new CoreConfig());
        assertThrows(InvalidFormatException.class, () -> {
            CoreConfigLoader coreConfigLoader = new CoreConfigLoader("/core-config/core-config-3.json");
            coreConfig.set(coreConfigLoader.load());
        });
    }
}
