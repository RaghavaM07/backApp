package daemon.Backup.Copier;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class DirectoryCopier extends SimpleFileVisitor<Path> implements ICopier {
    private final Path fromPath;
    private final Path toPath;
    // TODO: add progress tracker here

    public DirectoryCopier(Path fromPath, Path toPath) {
        this.fromPath = fromPath;
        this.toPath = toPath;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        // Compute the relative path and resolve it under the destination
        Path targetDir = toPath.resolve(fromPath.relativize(dir));

        // Create target directory if it doesn't exist
        if (!Files.exists(targetDir)) Files.createDirectories(targetDir);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        // Determine the target file path
        Path targetFile = toPath.resolve(fromPath.relativize(file));

        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);

        return FileVisitResult.CONTINUE;
    }

    @Override
    public void backup() throws IllegalArgumentException, IOException {
        // Validate that the source path is indeed a directory
        if (!Files.isDirectory(fromPath)) throw new IllegalArgumentException("Source must be a valid directory");

        // Create the destination folder if it doesn't exist
        if (!Files.exists(toPath)) Files.createDirectories(toPath);

        // Use Files.walkFileTree for recursive traversal and copying
        Files.walkFileTree(fromPath, this);
    }
}
