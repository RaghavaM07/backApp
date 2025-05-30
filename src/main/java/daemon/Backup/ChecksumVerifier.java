package daemon.Backup;

import daemon.Backup.FileTree.BaseNode;
import daemon.Backup.FileTree.FileNode;
import daemon.Utils;
import logger.LoggerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ChecksumVerifier {
    private static final Logger logger = LoggerUtil.getLogger(ChecksumVerifier.class);

    private final BaseNode root;
    public ChecksumVerifier(BaseNode root) {
        this.root = root;
    }

    public List<FileNode> verifyTree() {
        logger.info("Starting verification from " + root.getSrcName());
        List<FileNode> unmatched = new ArrayList<>();
        _isOk(root, unmatched);
        return unmatched;
    }
    private boolean _isOk(BaseNode root, List<FileNode> unmatched) {
        if(root == null) return true;
        if(root instanceof FileNode) {
            if(!checksumMatches((FileNode) root)) {
                logger.warning("Checksum did not match for `" + root.getSrcName() + "` after copy, will add to unmatched list");
                unmatched.add((FileNode) root);
            }
            logger.fine("Checksum matched for " + root.getSrcName());
            return true;
        }

        boolean retVal = true;
        for(BaseNode child: root.getChildren()) {
            retVal &= _isOk(child, unmatched);
        }
        return retVal;
    }

    private boolean checksumMatches(FileNode node) {
        return node.getChecksum() == Utils.calculateChecksum(node.getDestName());
    }
}
