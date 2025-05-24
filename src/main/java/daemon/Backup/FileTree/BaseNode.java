package daemon.Backup.FileTree;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.nio.file.Path;
import java.util.ArrayList;

@NoArgsConstructor
@Getter
public abstract class BaseNode {
    protected Path srcName;
    protected Path destName;

    protected ArrayList<BaseNode> children;

    public BaseNode(Path srcName, Path destName) {
        this.srcName = srcName;
        this.destName = destName;
    }

    public boolean addChild(BaseNode node) {
        return true;
    }

    public String printTree() {
        StringBuilder str = new StringBuilder();
        _printTree(this, 0, str);
        return str.toString();
    }
    private void _printTree(BaseNode root, int level, StringBuilder str) {
        if(root == null) return;

        str.append("\t".repeat(Math.max(0, level)));

        str.append(root.destName.getFileName()).append("\n");
        for (BaseNode node: root.children) {
            _printTree(node, level+1, str);
        }
    }
}
