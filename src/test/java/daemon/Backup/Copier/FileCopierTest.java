package daemon.Backup.Copier;

import daemon.Config.BackupConfig;
import daemon.Utils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static java.nio.file.Files.readAllBytes;
import static org.junit.jupiter.api.Assertions.*;

public class FileCopierTest {
    private final BackupConfig config1; {
        config1 = new BackupConfig();
        config1.setName("Config1");
        config1.setFromLocation("src/test/resources/copy-files/fromDir/test1.txt");
        config1.setToLocation("src/test/resources/copy-files/toDir/test1.txt");
    }
    private final BackupConfig config2; {
        config2 = new BackupConfig();
        config2.setName("Config2");
        config2.setFromLocation("src/test/resources/copy-files/fromDir/test2.txt");
        config2.setToLocation("src/test/resources/copy-files/toDir/test2.txt");
    }
    private final BackupConfig config3; {
        config3 = new BackupConfig();
        config3.setName("Config2");
        config3.setFromLocation("src/test/resources/copy-files/fromDir/DNE.txt");
        config3.setToLocation("src/test/resources/copy-files/toDir/NO_MATTER.txt");
    }

    private static void cleanDestDir() {
        File dir = new File("src/test/resources/copy-files/toDir/");
        File[] files = dir.listFiles();
        if(files == null) return;
        for (File file: files) {
            if (!file.isDirectory()) file.delete();
        }
    }
    @BeforeAll  static void initialCleanup()    { cleanDestDir(); }
    @AfterAll   static void finalCleanup()      { cleanDestDir(); }

    @Test
    void copiesFileSuccessfully() throws IOException {
        File fromPath = new File(config1.getFromLocation());
        File toPath = new File(config1.getToLocation());
        FileCopier copier = new FileCopier(fromPath.toPath(), toPath.toPath());
        try {
            copier.backup();
        } catch (IOException e) {
            Utils.errorHandler(e);
        }

        assertTrue(toPath.exists());
        assertEquals(toPath.length(), fromPath.length());
        assertArrayEquals(readAllBytes(toPath.toPath()), readAllBytes(fromPath.toPath()));
    }

    @Test
    void copiesEmptyFileSuccessfully() throws IOException {
        File fromPath = new File(config2.getFromLocation());
        File toPath = new File(config2.getToLocation());
        FileCopier copier = new FileCopier(fromPath.toPath(), toPath.toPath());
        try {
            copier.backup();
        } catch (IOException e) {
            Utils.errorHandler(e);
        }

        assertTrue(toPath.exists());
        assertEquals(toPath.length(), fromPath.length());
        assertArrayEquals(readAllBytes(toPath.toPath()), readAllBytes(fromPath.toPath()));
    }

    @Test
    void failsForDneFile() {
        File fromPath = new File(config3.getFromLocation());
        File toPath = new File(config3.getToLocation());
        FileCopier copier = new FileCopier(fromPath.toPath(), toPath.toPath());

        assertThrows(IOException.class, copier::backup);
    }
}
