package daemon.Backup.Compression;

import java.io.File;
import java.nio.file.Path;

public abstract class Compressor {
    protected File input;
    protected File output;

    public Compressor(File input, File output) {
        this.input = input;
        this.output = output;
    }
    public Compressor(Path input, Path output) {
        this.input = input.toFile();
        this.output = output.toFile();
    }

    public abstract void compress();
}
