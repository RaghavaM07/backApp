package daemon.Backup;

import daemon.Backup.FileTree.BaseNode;
import daemon.Backup.FileTree.FileNode;
import daemon.Backup.FileTree.FolderNode;
import logger.LoggerUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class TreeCopierTest {
    @BeforeAll
    static void init() {
        LoggerUtil.init(Level.INFO, "");
        File folderCopyDest = new File("src/test/resources/copy-folders/toDir");
        File fileCopyDest = new File("src/test/resources/copy-files/toDir");

        purgeDirectory(folderCopyDest);
        purgeDirectory(fileCopyDest);
    }
    @AfterAll
    static void cleanup() {
        File folderCopyDest = new File("src/test/resources/copy-folders/toDir");
        File fileCopyDest = new File("src/test/resources/copy-files/toDir");

        purgeDirectory(folderCopyDest);
        purgeDirectory(fileCopyDest);
    }

    static void purgeDirectory(File dir) {
        for (File file: dir.listFiles()) {
            if (file.isDirectory())
                purgeDirectory(file);
            file.delete();
        }
    }

    @Test
    void copiesSingleFile() throws IOException {
        Path src = new File("src/test/resources/copy-files/fromDir/test1.txt").toPath();
        Path dest = new File("src/test/resources/copy-files/toDir").toPath();

        Discoverer disc = new Discoverer(src, dest);
        BaseNode root = disc.discover();
        assertInstanceOf(FileNode.class, root);

        TreeCopier.copyTree(root);

        BaseNode output = new Discoverer(dest.resolve("test1.txt"), dest).discover();
        assertEquals(root.printTree(), output.printTree());
    }

    @Test
    void copiesSingleLevelFolder() throws IOException {
        Path src = new File("src/test/resources/copy-folders/fromDir/test2/").toPath();
        Path dest = new File("src/test/resources/copy-folders/toDir").toPath();

        Discoverer disc = new Discoverer(src, dest);
        BaseNode root = disc.discover();
        assertInstanceOf(FolderNode.class, root);

        TreeCopier.copyTree(root);

        BaseNode output = new Discoverer(dest.resolve("test2/"), dest).discover();
        assertEquals(root.printTree(), output.printTree());
    }

    @Test
    void copiesMultiLevelFolder() throws IOException {
        Path src = new File("src/test/resources/copy-folders/fromDir/test1/").toPath();
        Path dest = new File("src/test/resources/copy-folders/toDir").toPath();

        Discoverer disc = new Discoverer(src, dest);
        BaseNode root = disc.discover();
        assertInstanceOf(FolderNode.class, root);

        TreeCopier.copyTree(root);

        BaseNode output = new Discoverer(dest.resolve("test1/"), dest).discover();
        assertEquals(root.printTree(), output.printTree());
    }
}
