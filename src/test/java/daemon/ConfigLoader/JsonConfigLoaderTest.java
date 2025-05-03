package daemon.ConfigLoader;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import daemon.Config.BackupConfig;
import daemon.Config.CompressionType;
import daemon.Config.CoreConfig;
import daemon.Constants;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public class JsonConfigLoaderTest {

    @Test
    void loadsGoodFile() {
        BackupConfig backupConfig = new BackupConfig();

        try {
            JsonConfigLoader loader = new JsonConfigLoader("src/test/resources/json-config/bkp-config-1.json");
            backupConfig = loader.load();
        } catch (Exception e) {
            System.out.println("Caught exception: "+ e.getClass() + " - " + e.getMessage());
        }

        assertEquals("Test Backup 1", backupConfig.getName());
        assertEquals("/home/raghava/importantFiles/", backupConfig.getFromLocation());
        assertEquals("/mnt/backups/myBackups/", backupConfig.getToLocation());
        assertEquals(0, backupConfig.getInterval().getDays());
        assertEquals(5, backupConfig.getInterval().getHours());
        assertEquals(30, backupConfig.getInterval().getMinutes());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            assertEquals(df.parse("2025-12-14 05:35 AM"), backupConfig.getLastTime());
        } catch (ParseException e) {
            System.out.println("Caught exception: "+ e.getClass() + " - " + e.getMessage());
            fail();
        }
        assertEquals(CompressionType.GZIP, backupConfig.getCompression());
    }

    @Test
    void loadsDefaultValues() {
        BackupConfig backupConfig = new BackupConfig();

        try {
            JsonConfigLoader loader = new JsonConfigLoader("src/test/resources/json-config/bkp-config-2.json");
            backupConfig = loader.load();
        } catch (Exception e) {
            System.out.println("Caught exception: "+ e.getClass() + " - " + e.getMessage());
        }

        assertEquals("Test Backup 2", backupConfig.getName());
        assertEquals("/home/raghava/importantFiles/", backupConfig.getFromLocation());
        assertEquals(Constants.DEFAULT_BACKUP_DIR, backupConfig.getToLocation());
        assertEquals(1, backupConfig.getInterval().getDays());
        assertEquals(0, backupConfig.getInterval().getHours());
        assertEquals(0, backupConfig.getInterval().getMinutes());
        assertNull(backupConfig.getLastTime());
        assertEquals(CompressionType.NONE, backupConfig.getCompression());
    }

    @Test
    void throwsExceptionForInvalidValues() {
        AtomicReference<BackupConfig> bkpConfig = new AtomicReference<>(new BackupConfig());
        assertThrows(InvalidFormatException.class, () -> {
            JsonConfigLoader loader = new JsonConfigLoader("src/test/resources/json-config/bkp-config-3.json");
            bkpConfig.set(loader.load());
        });
    }
}
