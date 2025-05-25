package daemon.Backup;

import daemon.Backup.FileTree.BaseNode;
import daemon.Backup.FileTree.FileNode;
import daemon.Backup.FileTree.FolderNode;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.Deque;

public class Discoverer extends SimpleFileVisitor<Path> {
    private final Path srcBase;
    private final Path destBase;
    private final BaseNode root;

    private final Deque<FolderNode> stack = new ArrayDeque<>();

    public Discoverer(Path srcPath, Path destPath) {
        this.srcBase = srcPath;
        this.destBase = destPath;

        Path rootDest = destBase.resolve(srcBase.getFileName());

        if (Files.isDirectory(srcBase)) {
            FolderNode folder = new FolderNode(srcBase, rootDest);
            this.root = folder;
            stack.push(folder);
        } else {
            this.root = new FileNode(srcBase, rootDest);
        }
    }

    private Path computeDest(Path current) {
        Path relativePath = srcBase.relativize(current);
        return destBase.resolve(srcBase.getFileName()).resolve(relativePath);
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (dir.equals(srcBase)) {
            return FileVisitResult.CONTINUE;
        }

        FolderNode folder = new FolderNode(dir, computeDest(dir));
        FolderNode parent = stack.peek();
        parent.addChild(folder);
        stack.push(folder);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (!Files.isDirectory(srcBase)) {
            return FileVisitResult.CONTINUE;
        }

        FileNode fileNode = new FileNode(file, computeDest(file));
        FolderNode parent = stack.peek();
        parent.addChild(fileNode);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (Files.isDirectory(srcBase) && !dir.equals(srcBase)) {
            stack.pop();
        }
        return FileVisitResult.CONTINUE;
    }

    public BaseNode discover() throws IOException {
        if (Files.isDirectory(srcBase)) {
            Files.walkFileTree(srcBase, this);
        }
        return root;
    }
}
