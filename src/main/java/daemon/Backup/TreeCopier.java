package daemon.Backup;

import daemon.Backup.FileTree.BaseNode;
import daemon.Backup.FileTree.FolderNode;
import daemon.Utils;
import logger.LoggerUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class TreeCopier {
    private static final Logger logger = LoggerUtil.getLogger(TreeCopier.class);

    public static void copyTree(BaseNode root) {
        String src = root.getSrcName().toString();
        String dest = root.getDestName().toString();

        logger.info("Starting copy " + src + " -> " + dest);
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<Void> future = copyNode(root, executor);
            future.join();
            logger.info("Copy complete " + src + " -> " + dest);
        } catch (Exception e) {
            logger.severe("Error during copy from " + src);
            throw e;
        }
    }

    private static CompletableFuture<Void> copyNode(BaseNode root, ExecutorService executor) {
        return CompletableFuture.runAsync(() -> {
            Path src = root.getSrcName();
            Path dest = root.getDestName();
            // copy root first
            try {
                if (root instanceof FolderNode) {
                    logger.info("Creating directory: " + dest);
                    Files.createDirectories(dest);
                } else {
                    logger.info("Copying file: " + src + " -> " + dest);
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executor).thenCompose(v -> { // copied root, now recurse it's children
            List<BaseNode> children = root.getChildren();
            if (children != null && !children.isEmpty()) {
                List<CompletableFuture<Void>> childFutures = new ArrayList<>();
                for (BaseNode child : children) {
                    childFutures.add(copyNode(child, executor));
                }

                return CompletableFuture.allOf(childFutures.toArray(new CompletableFuture[0]));
            } else {
                return CompletableFuture.completedFuture(null);
            }
        });
    }
}
