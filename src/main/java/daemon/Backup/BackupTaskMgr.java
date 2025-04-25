package daemon.Backup;

import daemon.Config.BackupConfig;
import daemon.Config.CoreConfig;

import java.util.concurrent.*;

public class BackupTaskMgr {
    private static BackupTaskMgr instance = null;
    private final ScheduledExecutorService scheduler;
    private final ConcurrentHashMap<BackupConfig, ScheduledFuture<?>> taskHandlerMap;

    private BackupTaskMgr(short maxThreads) {
        this.scheduler = Executors.newScheduledThreadPool(maxThreads);
        this.taskHandlerMap = new ConcurrentHashMap<>();
    }

    public static synchronized BackupTaskMgr getInstance(CoreConfig config) {
        if(config == null || BackupTaskMgr.instance != null) return instance;

        BackupTaskMgr.instance = new BackupTaskMgr(config.getMaxThreads());
        return instance;
    }

    public void scheduleNewBackup(BackupConfig config) {
        System.out.println("Scheduling: " + config);
        BackupTask task = new BackupTask(config);
        ScheduledFuture<?> taskHandler = scheduler.scheduleAtFixedRate(task, 0,
                                                                       config.getInterval().toMillis(), TimeUnit.MILLISECONDS);
        taskHandlerMap.put(config, taskHandler);
    }

    public int getTaskCnt() {
        return taskHandlerMap.size();
    }

    public void cancelTask(BackupConfig config) {
        taskHandlerMap.get(config).cancel(true);
    }

    public void shutdownScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
