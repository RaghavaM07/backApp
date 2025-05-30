package daemon.Backup.FileTree;

import daemon.Utils;
import lombok.Getter;

import java.nio.file.Path;
import java.util.ArrayList;

@Getter
public class FileNode extends BaseNode {
    private final long checksum;

    public FileNode(Path srcName, Path destName) {
        super(srcName, destName);
        this.children = new ArrayList<>();
        this.checksum = Utils.calculateChecksum(srcName);
    }
}
