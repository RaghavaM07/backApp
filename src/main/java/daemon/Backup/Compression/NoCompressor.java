package daemon.Backup.Compression;

import java.io.File;
import java.nio.file.Path;

public class NoCompressor extends Compressor{
    public NoCompressor(File input, File output) {
        super(input, output);
    }
    public NoCompressor(Path input, Path output) {
        super(input, output);
    }

    @Override
    public void compress() {
        // Do nothing, no compression
    }
}
