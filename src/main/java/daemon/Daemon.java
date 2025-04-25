package daemon;

import daemon.Backup.BackupTaskMgr;
import daemon.Config.BackupConfig;
import daemon.Config.CoreConfig;
import daemon.ConfigLoader.CoreConfigLoader;
import daemon.ConfigLoader.JsonConfigLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;


public class Daemon implements Runnable{
    public void run() {
        /*
         * PLAN:
         * read the base config file for daemon config
         * spawn initial BackupTaskMgr object
         * read the backup config files for backup configs -> initial scan
         * start a WatchService in separate thread to monitor folder for create/update/delete ops on config files
         *       this thread will handle what to do with backup task threads (via mgr) when file events occur
         * construct all backup configs
         * schedule a task via mgr for each config
         *       each task will launch, update progress, do backup, set lastTime, join
         *
         * start socket server on main thread for potential client connections
         *       this server will have access to mgr object
         * */

        // //////////////////////////////////////////////////
        // TODO: Create and instantiate logger object here
        // //////////////////////////////////////////////////

        printAllConstants();    // only for debug, may extend for logging

        // first and foremost, read core config
        CoreConfig coreConfig = new CoreConfig();
        try {
            CoreConfigLoader coreLoader = new CoreConfigLoader(Constants.DEFAULT_CORE_CONFIG_FILE);
            coreConfig = coreLoader.load();
            System.out.println(coreConfig);
        } catch (Exception e) {
            errorHandler(e);
        }


        Path backupConfigFolder = Paths.get(coreConfig.getBackupConfigFileLocation());
        ConcurrentHashMap<String, BackupConfig> backupCfgFileObjMap = new ConcurrentHashMap<>();

        // initial scan
        File bkpCfgDir = new File(coreConfig.getBackupConfigFileLocation());
        boolean bkpCfgDirExists = bkpCfgDir.exists() && bkpCfgDir.isDirectory();
        if(bkpCfgDirExists) {
            try {
                Stream<String> backupConfigFiles = Files.walk(backupConfigFolder)
                        .filter(p -> Files.isRegularFile(p) && isFileSupported(p.getFileName().toString()))
                        .map(p -> String.valueOf(p.toAbsolutePath()));
                backupConfigFiles.forEach(file -> {
                    try {
                        backupCfgFileObjMap.put(file, new JsonConfigLoader(file).load());
                    } catch (Exception e) {
                        errorHandler(e);
                    }
                });
            } catch (Exception e) {
                errorHandler(e);
            }
        }

        // spawn FileWatcher Thread
        // TODO: FileWatcher (uses WatchService)

        BackupTaskMgr taskMgr = BackupTaskMgr.getInstance(coreConfig);
        for(BackupConfig config: backupCfgFileObjMap.values()) {

            taskMgr.scheduleNewBackup(config);
        }

        System.out.println("Running " + taskMgr.getTaskCnt() + " tasks");
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            errorHandler(e);
        }
    }

    private final String[] supportedExtns = {".json"};
    private boolean isFileSupported(String s) {
        return Arrays.stream(supportedExtns).anyMatch(s::endsWith);
    }

    private void errorHandler(Exception e) {
        System.err.println("===========================================");
        System.err.print("ERROR Occurred: ");
        System.err.println(e.getClass());
        System.err.println(e.getMessage());
        System.exit(1);
    }

    // for debug
    private void printAllConstants() {
        try {
            for (Field field : Constants.class.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getType().equals(String.class)) {
                    System.out.println(field.getName() + " = " + field.get(null));
                }
            }
        } catch (Exception e) {
            errorHandler(e);
        }
    }
}
