package daemon.Backup.FileTree;

import lombok.Getter;

import java.nio.file.Path;
import java.util.ArrayList;

@Getter
public class FolderNode extends BaseNode {
    public FolderNode(Path srcName, Path destName) {
        super(srcName, destName);
        this.children = new ArrayList<>();
    }

    @Override
    public boolean addChild(BaseNode node) {
        return children.add(node);
    }
}
