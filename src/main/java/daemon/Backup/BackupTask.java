package daemon.Backup;

import daemon.Backup.Copier.DirectoryCopier;
import daemon.Backup.Copier.FileCopier;
import daemon.Backup.Copier.ICopier;
import daemon.Config.BackupConfig;
import daemon.Utils;
import logger.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    @Override
    public void run() {
        logger.info("Starting task: " + config.getName());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss");
        String date = simpleDateFormat.format(new Date());

        Path fromPath = new File(config.getFromLocation()).toPath();
        Path toPath = new File(config.getToLocation() + "-bkp-" + date).toPath();
        ICopier copier = null;

        if(Files.isRegularFile(fromPath)) copier = new FileCopier(fromPath, toPath);
        else copier = new DirectoryCopier(fromPath, toPath);

        try {
            copier.backup();
            logger.info("Done task: " + config.getName());
        } catch (IOException e) {
            Utils.errorHandler(e);
        }
    }
}
