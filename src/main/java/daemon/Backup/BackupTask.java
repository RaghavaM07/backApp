package daemon.Backup;

import daemon.Backup.Compression.Compressor;
import daemon.Backup.Compression.CompressorFactory;
import daemon.Backup.Compression.NoCompressor;
import daemon.Backup.FileTree.BaseNode;
import daemon.Backup.FileTree.FileNode;
import daemon.Config.BackupConfig;
import daemon.Utils;
import logger.LoggerUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


public class BackupTask implements Runnable {
    private static final Logger logger = LoggerUtil.getLogger(BackupTask.class);
    private final BackupConfig config;

    public BackupTask(BackupConfig config) {
        this.config = config;
    }

    // When daemon restarts, we don't want to re-run a task that need not be run again at this time
    private boolean shouldRun() {
        boolean retVal = config.getLastTime() == null ||
                System.currentTimeMillis()-config.getLastTime().getTime() >= config.getInterval().toMillis();

        if(!retVal) {
            logger.warning(String.format("%s was last run at %s, next run expected at %s",
                    config.getName(),
                    config.getLastTime(),
                    new Date(config.getLastTime().getTime() + config.getInterval().toMillis())
            ));
        }

        return retVal;
    }

    // IDEA: split into multiple stages:
    // 1. get source file list (file path), their checksums (crc32) and generate output file paths
    // 2. copiers now copy only these pre-discovered files to target folder
    // 3. verify file checksums, if inconsistent - redo copy of that file
    // 4. compression pass
    @Override
    public void run() {
        if(!shouldRun()) return;

        logger.info("Starting task: " + config.getName());

        // discover folders and files
        Discoverer discoverer = getDiscoverer();
        BaseNode root;
        try {
            root = discoverer.discover();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // copy files
        TreeCopier.copyTree(root);

        // verify copied files' checksums
        ChecksumVerifier verifier = new ChecksumVerifier(root);
        List<FileNode> unmatched = verifier.verifyTree();
        if(!unmatched.isEmpty()) {
            // Some files didn't copy correctly, copy them once again
            for (FileNode node: unmatched) {
                // Check if file might have been removed
                if(!(new File(node.getSrcName().toString()).exists())) {
                    logger.warning(node.getSrcName() + " does not exist anymore, skipping");
                    continue;
                }
                // if file exists, we should be ideally able to re-copy it
                logger.warning("Copying " + node.getSrcName() + " again to " + node.getDestName());
                TreeCopier.copyTree(node);
                if(!(new ChecksumVerifier(node).verifyTree()).isEmpty()) logger.severe("Re-copy failed for " + node.getSrcName() + ", continuing");
            }
        }

        // compress backup
        String archiveExtension = CompressorFactory.getArchiveExtension(config.getCompression());
        String archiveLocation = root.getDestName().toFile().getAbsolutePath() + archiveExtension;
        Compressor compressor = CompressorFactory.getCompressor(config.getCompression(), root.getDestName(), Path.of(archiveLocation));
        compressor.compress();
        logger.info("Compression phase output for " + config.getName() + " at " + archiveLocation);
        if(!(compressor instanceof NoCompressor)){
            logger.info("Removing original file post compression " + root.getDestName());
            try {
                FileUtils.deleteDirectory(root.getDestName().toFile());
            } catch (IOException e) {
                Utils.errorHandler(e);
            }
        }

        // old backup cleanup
        logger.info("Enforcing maxRetention copies = " + config.getMaxRetention());
        FolderCleanup cleaner = new FolderCleanup(config.getMaxRetention(), root.getDestName().getParent().getParent().toFile(), root.getSrcName().getFileName().toString());
        cleaner.cleanup();

        // update backup conf file with lastTime = current time
        config.setLastTime(Date.from(Instant.now()));
    }

    private Discoverer getDiscoverer() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy-hhmmss");
        String date = simpleDateFormat.format(new Date());

        Path fromPath = new File(config.getFromLocation()).toPath();
        String sourceName = fromPath.getFileName().toString();
        String destName = sourceName + String.format("-%s.bkp", date);
        Path toPath = new File(config.getToLocation()).toPath().resolve(destName);

        return new Discoverer(fromPath, toPath);
    }
}
