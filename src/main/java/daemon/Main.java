package daemon;

public class Main {
    public static void main(String[] args) {
        /*
        * PLAN:
        * read the base config file for daemon config
        * read the backup config file for backups config
        * construct all backup configs
        * make a priority queue of backups, sorted by lowest nextTime first
        * spawn threads when it is time
        *   these threads will launch, update progress, do backup, set lastTime, join
        *
        * start socket server on main thread for potential client connections
        * */



    }
}
