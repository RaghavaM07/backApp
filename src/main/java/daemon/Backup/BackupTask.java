package daemon.Backup;

import daemon.Backup.FileTree.BaseNode;
import daemon.Config.BackupConfig;
import daemon.Utils;
import logger.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        Discoverer discoverer = getDiscoverer();
        BaseNode root;
        try {
            root = discoverer.discover();
        } catch (IOException e) {
            Utils.errorHandler(e);
        }

        // TODO: copy files
        // TODO: verify copied files' checksums
        // TODO: compress backup
        logger.info("Done task: " + config.getName());
        logger.info("Enforcing maxRetention copies = " + config.getMaxRetention());
        // TODO: implement old backup cleanup
        // TODO: update backup conf file with lastTime = current time and override file
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
