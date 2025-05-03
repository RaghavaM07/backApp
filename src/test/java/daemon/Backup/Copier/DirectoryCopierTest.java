package daemon.Backup.Copier;

import daemon.Config.BackupConfig;
import daemon.Utils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class DirectoryCopierTest {
    private final BackupConfig config1; {
        config1 = new BackupConfig();
        config1.setName("Config1");
        config1.setFromLocation("src/test/resources/copy-folders/fromDir/test1/");
        config1.setToLocation("src/test/resources/copy-folders/toDir/test1-bkp/");
    }
    private final BackupConfig config2; {
        config2 = new BackupConfig();
        config2.setName("Config2");
        config2.setFromLocation("src/test/resources/copy-folders/fromDir/test2/");
        config2.setToLocation("src/test/resources/copy-folders/toDir/test2-bkp/");
    }
    private final BackupConfig config3; {
        config3 = new BackupConfig();
        config3.setName("Config3");
        config3.setFromLocation("src/test/resources/copy-folders/fromDir/test3/");
        config3.setToLocation("src/test/resources/copy-folders/toDir/test3-bkp/");
    }

    private static void deleteDir(File file) {
        if(!file.exists()) return;
        File[] contents = file.listFiles();
        if (contents != null) for (File f : contents) deleteDir(f);
        file.delete();
    }
    private static void deleteTestDirs() {
        deleteDir(new File("src/test/resources/copy-folders/toDir/test1-bkp/"));
        deleteDir(new File("src/test/resources/copy-folders/toDir/test2-bkp/"));
        deleteDir(new File("src/test/resources/copy-folders/toDir/test3-bkp/"));
    }
    @BeforeAll  static void initialCleanup()    { deleteTestDirs(); }
    @AfterAll   static void finalCleanup()      { deleteTestDirs(); }

    @Test
    void copiesFolderSuccessfully() throws IOException {
        Path fromPath = new File(config1.getFromLocation()).toPath();
        Path toPath = new File(config1.getToLocation()).toPath();

        DirectoryCopier copier = new DirectoryCopier(fromPath, toPath);
        try {
            copier.backup();
        } catch (IOException e) {
            Utils.errorHandler(e);
        }

        assertTrue(Files.exists(toPath));

        Path test1RootFile = toPath.resolve("test1-root.txt");
        assertTrue(Files.exists(test1RootFile));
        assertArrayEquals(Files.readAllBytes(test1RootFile), Files.readAllBytes(fromPath.resolve("test1-root.txt")));

        Path test1_1 = toPath.resolve("test1.1");
        {
            Path test1_1RootFile = test1_1.resolve("test1.1-root.txt");
            assertTrue(Files.exists(test1_1) && Files.isDirectory(test1_1));
            assertTrue(Files.exists(test1_1RootFile));
            assertArrayEquals(Files.readAllBytes(test1_1RootFile), Files.readAllBytes(fromPath.resolve("test1.1/test1.1-root.txt")));
        }

        Path test1_2 = toPath.resolve("test1.2");
        {
            assertTrue(Files.exists(test1_2) && Files.isDirectory(test1_2));

            {
                Path test1_2_1 = test1_2.resolve("test1.2.1");
                Path test1_2_1RootFile = test1_2_1.resolve("test1.2.1-root.txt");
                assertTrue(Files.exists(test1_2_1) && Files.isDirectory(test1_2_1));
                assertTrue(Files.exists(test1_2_1RootFile));
                assertArrayEquals(Files.readAllBytes(test1_2_1RootFile), Files.readAllBytes(fromPath.resolve("test1.2/test1.2.1/test1.2.1-root.txt")));
            }
        }
    }

    @Test
    void copiesSingleLevelDeepFolder() throws IOException {
        Path fromPath = new File(config2.getFromLocation()).toPath();
        Path toPath = new File(config2.getToLocation()).toPath();

        DirectoryCopier copier = new DirectoryCopier(fromPath, toPath);
        try {
            copier.backup();
        } catch (IOException e) {
            Utils.errorHandler(e);
        }

        assertTrue(Files.exists(toPath));

        Path test2RootFile = toPath.resolve("test2-root.txt");
        assertTrue(Files.exists(test2RootFile));
        assertArrayEquals(Files.readAllBytes(test2RootFile), Files.readAllBytes(fromPath.resolve("test2-root.txt")));
    }

    @Test
    void copiesEmptyFolder() {
        Path fromPath = new File(config3.getFromLocation()).toPath();
        Path toPath = new File(config3.getToLocation()).toPath();

        DirectoryCopier copier = new DirectoryCopier(fromPath, toPath);
        try {
            copier.backup();
        } catch (IOException e) {
            Utils.errorHandler(e);
        }

        assertTrue(Files.exists(toPath));
        assertEquals(fromPath.toFile().length(), toPath.toFile().length()); // length = 0
    }
}
