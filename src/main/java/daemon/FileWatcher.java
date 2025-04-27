package daemon;

import daemon.Backup.BackupTaskMgr;
import daemon.Config.BackupConfig;
import daemon.ConfigLoader.JsonConfigLoader;
import logger.LoggerUtil;

import java.io.File;
import java.nio.file.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher implements Runnable{
    private static final Logger logger = LoggerUtil.getLogger(FileWatcher.class);
    private final String configDir;
    private final ConcurrentHashMap<String, BackupConfig> backupCfgFileObjMap;
    private final BackupTaskMgr mgr;

    public FileWatcher(String configDir, ConcurrentHashMap<String, BackupConfig> backupCfgFileObjMap) throws Exception {
        this.configDir = configDir;
        this.backupCfgFileObjMap = backupCfgFileObjMap;
        this.mgr = BackupTaskMgr.getInstance(null);
        if(mgr == null) {
            throw new Exception("Could not instantiate FileWatcher. mgr was null");
        }
    }

    private void handleNewConfigFile(File file) {
        String filePath = file.getAbsolutePath();
        logger.info("Creating new config for file: " + filePath);
        try {
            BackupConfig config = new JsonConfigLoader(filePath).load();
            mgr.scheduleNewBackup(config);
            backupCfgFileObjMap.put(filePath, config);
        } catch (Exception e) {
            Utils.errorHandler(e);
        }
    }
    private void handleConfigFileDelete(File file) {
        String filePath = file.getAbsolutePath();
        logger.info("Removing current task due to config removal/file update: " + filePath);

        BackupConfig config = backupCfgFileObjMap.get(filePath);
        mgr.cancelTask(config);
        backupCfgFileObjMap.remove(filePath);
    }
    private void handleConfigFileUpdate(File file) {
        String filePath = file.getAbsolutePath();
        logger.info("Config file updated: " + filePath);

        BackupConfig oldConfig = backupCfgFileObjMap.get(filePath);
        if(oldConfig != null) {
            mgr.cancelTask(oldConfig);
            backupCfgFileObjMap.remove(filePath);
        }

        try {
            BackupConfig newConfig = new JsonConfigLoader(filePath).load();
            backupCfgFileObjMap.put(filePath, newConfig);
            mgr.scheduleNewBackup(newConfig);
        } catch (Exception e) {
            Utils.errorHandler(e);
        }
    }

    @Override
    public void run() {
        Path dir = Paths.get(configDir);
        try(WatchService watchService = FileSystems.getDefault().newWatchService()) {
            dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            logger.info("FileWatcher registered on " + configDir);

            while(true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // The filename is the context of the event
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                    File file = dir.resolve(fileName).toFile();

                    if (!Utils.isFileSupported(fileName.toString())) break;

                    if (kind == ENTRY_CREATE) {
                        logger.info("New config file created: " + fileName);
                        handleNewConfigFile(file);
                        break;
                    } else if (kind == ENTRY_MODIFY) {
                        logger.warning("Config file modified: " + fileName);
                        // For updates, update the existing config instance
                        handleConfigFileUpdate(file);
                        break;
                    } else if (kind == ENTRY_DELETE) {
                        logger.warning("Config file deleted: " + fileName);
                        // Remove from the current config map
                        handleConfigFileDelete(file);
                        break;
                    }
                }

                if (!key.reset()) {
                    logger.severe("WatchKey was not reset. Exiting watch loop.");
                    break;
                }
            }
        } catch (Exception e) {
            Utils.errorHandler(e);
        }
    }
}
