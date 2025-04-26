package daemon.Backup;

import daemon.Config.BackupConfig;
import daemon.Config.CoreConfig;
import logger.LoggerUtil;

import java.util.concurrent.*;
import java.util.logging.Logger;

public class BackupTaskMgr {
    private static final Logger logger = LoggerUtil.getLogger(BackupTaskMgr.class);
    private static BackupTaskMgr instance = null;
    private final ScheduledExecutorService scheduler;
    private final ConcurrentHashMap<BackupConfig, ScheduledFuture<?>> taskHandlerMap;

    private BackupTaskMgr(short maxThreads) {
        logger.info("Creating BackupTaskMgr with pool size: " + maxThreads);
        this.scheduler = Executors.newScheduledThreadPool(maxThreads);
        this.taskHandlerMap = new ConcurrentHashMap<>();
    }

    public static synchronized BackupTaskMgr getInstance(CoreConfig config) {
        if(config == null || BackupTaskMgr.instance != null) return instance;

        BackupTaskMgr.instance = new BackupTaskMgr(config.getMaxThreads());
        return instance;
    }

    public void scheduleNewBackup(BackupConfig config) {
        logger.info("Scheduling: " + config);
        BackupTask task = new BackupTask(config);
        ScheduledFuture<?> taskHandler = scheduler.scheduleAtFixedRate(task, 0,
                                                                       config.getInterval().toMillis(), TimeUnit.MILLISECONDS);
        taskHandlerMap.put(config, taskHandler);
    }

    public int getTaskCnt() {
        return taskHandlerMap.size();
    }

    public void cancelTask(BackupConfig config) {
        logger.warning("Cancelling task: " + config);
        taskHandlerMap.get(config).cancel(true);
    }

    public void shutdownScheduler() {
        logger.warning("Shutting down scheduler");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                logger.warning("Force shutting down scheduler");
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
