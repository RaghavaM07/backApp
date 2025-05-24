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

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy-hhmmss");
        String date = simpleDateFormat.format(new Date());

        Path fromPath = new File(config.getFromLocation()).toPath();
        String sourceName = fromPath.getFileName().toString();
        String destName = sourceName + String.format("-%s.bkp", date);
        Path toPath = new File(config.getToLocation()).toPath().resolve(destName);
        ICopier copier = null;

        if(Files.isRegularFile(fromPath)) copier = new FileCopier(fromPath, toPath);
        else copier = new DirectoryCopier(fromPath, toPath);

        try {
            copier.backup();
            logger.info("Done task: " + config.getName());
            logger.info("Enforcing maxRetention copies = " + config.getMaxRetention());
            // TODO: implement old backup cleanup
        } catch (IOException e) {
            Utils.errorHandler(e);
        }
    }
}
