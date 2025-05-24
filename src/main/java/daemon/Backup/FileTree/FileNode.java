package daemon.Backup.FileTree;

import daemon.Utils;
import lombok.Getter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

@Getter
public class FileNode extends BaseNode {
    private final long checksum;

    public FileNode(Path srcName, Path destName) {
        super(srcName, destName);
        this.children = new ArrayList<>();
        this.checksum = calcChecksum();
    }

    private long calcChecksum() {
        CRC32 crc = new CRC32();
        // Open the file as an InputStream and wrap it in a CheckedInputStream.
        try (InputStream fis = Files.newInputStream(srcName);
             CheckedInputStream cis = new CheckedInputStream(fis, crc)) {
            byte[] buffer = new byte[1024];
            // Read through the file; the CheckedInputStream automatically updates the checksum.
            while (cis.read(buffer) != -1) {
                // Continue reading until EOF.
            }
        } catch (IOException e) {
            Utils.errorHandler(e);
        }
        return crc.getValue();
    }
}
