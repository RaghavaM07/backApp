package daemon.Backup;

import daemon.Backup.FileTree.BaseNode;
import daemon.Backup.FileTree.FileNode;
import daemon.Backup.FileTree.FolderNode;
import daemon.Utils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.CRC32;

import static org.junit.jupiter.api.Assertions.*;

public class DiscovererTest {

    @Test
    void testSingleFile() {
        Discoverer disc = new Discoverer(
                new File("src/test/resources/copy-files/fromDir/test1.txt").toPath(),
                new File("src/test/resources/copy-files/toDir").toPath()
        );

        try {
            BaseNode root = disc.discover();
            assertInstanceOf(FileNode.class, root);
            assertEquals("test1.txt", root.printTree().trim());
            assertEquals(new File("src/test/resources/copy-files/toDir/test1.txt").toPath().toString(), root.getDestName().toString());

            byte[] fileBytes = Files.readAllBytes(new File("src/test/resources/copy-files/fromDir/test1.txt").toPath());
            CRC32 expectedCrc = new CRC32();
            expectedCrc.update(fileBytes);
            long expectedChecksum = expectedCrc.getValue();
            assertEquals(expectedChecksum, ((FileNode)root).getChecksum());
        } catch (IOException e) {
            Utils.errorHandler(e);
        }
    }

    @Test
    void testSingleLevelFolder() {
        Discoverer disc = new Discoverer(
                new File("src/test/resources/copy-folders/fromDir/test2/").toPath(),
                new File("src/test/resources/copy-folders/toDir").toPath()
        );

        try {
            BaseNode root = disc.discover();
            assertInstanceOf(FolderNode.class, root);
            assertEquals("test2\n\ttest2-root.txt", root.printTree().trim());

            byte[] fileBytes = Files.readAllBytes(new File("src/test/resources/copy-folders/fromDir/test2/test2-root.txt").toPath());
            CRC32 expectedCrc = new CRC32();
            expectedCrc.update(fileBytes);
            long expectedChecksum = expectedCrc.getValue();
            assertEquals(expectedChecksum, ((FileNode)root.getChildren().getFirst()).getChecksum());
        } catch (IOException e) {
            Utils.errorHandler(e);
        }
    }

    @Test
    void testMultiLevelFolder() {
        Discoverer disc = new Discoverer(
                new File("src/test/resources/copy-folders/fromDir/test1/").toPath(),
                new File("src/test/resources/copy-folders/toDir").toPath()
        );

        try {
            BaseNode root = disc.discover();
            assertInstanceOf(FolderNode.class, root);
            // !!! VERIFY MANUALLY, LARGE STRING !!!
            assertEquals("test1\n" +
                    "\ttest1-root.txt\n" +
                    "\ttest1.1\n" +
                    "\t\ttest1.1-root.txt\n" +
                    "\ttest1.2\n" +
                    "\t\ttest1.2.1\n" +
                    "\t\t\ttest1.2.1-root.txt", root.printTree().trim());
        } catch (IOException e) {
            Utils.errorHandler(e);
        }
    }
}
