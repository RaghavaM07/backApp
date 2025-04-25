package daemon.Backup;

import daemon.Config.BackupConfig;


public class BackupTask implements Runnable {
    private BackupConfig config;

    public BackupTask(BackupConfig config) {
        this.config = config;
    }

    @Override
    public void run() {
        System.out.println("Starting task: " + config.getName());

        // TODO: Implement backup here
        try {
            Thread.sleep(5_000);
            System.out.println("Done task: " + config.getName());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
