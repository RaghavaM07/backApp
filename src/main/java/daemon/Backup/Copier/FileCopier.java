package daemon.Backup.Copier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileCopier implements ICopier{
    private final Path fromPath;
    private final Path toPath;
    // TODO: add progress tracker here

    public FileCopier(Path fromPath, Path toPath) {
        this.fromPath = fromPath;
        this.toPath = toPath;
    }

    @Override
    public void backup() throws IOException {
        if(!Files.exists(toPath)) Files.createFile(toPath);

        Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
    }
}
