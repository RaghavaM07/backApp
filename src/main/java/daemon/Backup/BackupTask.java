package daemon.Backup;

import daemon.Config.BackupConfig;
import logger.LoggerUtil;

import java.util.logging.Logger;


public class BackupTask implements Runnable {
    private static final Logger logger = LoggerUtil.getLogger(BackupTask.class);
    private final BackupConfig config;

    public BackupTask(BackupConfig config) {
        this.config = config;
    }

    @Override
    public void run() {
        logger.info("Starting task: " + config.getName());

        // TODO: Implement backup here
        try {
            Thread.sleep(5_000);
            logger.info("Done task: " + config.getName());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
