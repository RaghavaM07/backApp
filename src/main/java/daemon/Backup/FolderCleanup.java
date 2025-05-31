package daemon.Backup;

import daemon.Utils;
import logger.LoggerUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.logging.Logger;

public class FolderCleanup {
    private static final Logger logger = LoggerUtil.getLogger(FolderCleanup.class);
    private static class CustomComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");
            // extract dates
            int index1 = f1.getName().indexOf("-");
            int index2 = f2.getName().indexOf("-");
            String ts1 = f1.getName().substring(index1 + 1, f1.getName().length()-4);
            String ts2 = f2.getName().substring(index2 + 1, f2.getName().length()-4);
            try {
                LocalDateTime d1 = LocalDateTime.parse(ts1, dtFormatter);
                LocalDateTime d2 = LocalDateTime.parse(ts2, dtFormatter);
                // descending order
                return d2.compareTo(d1);
            } catch (DateTimeParseException e) {
                return 0;
            }
        }
    }

    private final short maxFolders;
    private final File parentFolder;
    private final String backupSeries;
    public FolderCleanup(short maxFolders, File parentFolder, String backupSeries) {
        this.maxFolders = maxFolders;
        this.parentFolder = parentFolder;
        this.backupSeries = backupSeries;
    }

    public int cleanup() {
        if (!parentFolder.exists() || !parentFolder.isDirectory()) {
            logger.severe("Provided path is not a valid directory: " + parentFolder);
            return -1;
        }

        File[] subFolders = parentFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File folder = new File(dir, name);
                return folder.isDirectory() && name.matches("^" + backupSeries + "-\\d{8}-\\d{6}.bkp$");
            }
        });
        if (subFolders == null || subFolders.length == 0) {
            logger.severe("No subfolders found in " + parentFolder.getAbsolutePath());
            return -1;
        }
        List<File> folderList = new ArrayList<>(Arrays.asList(subFolders));
        logger.info("Found " + folderList.size() + " backup folders");

        folderList.sort(new CustomComparator());

        int success=0;
        if (folderList.size() > maxFolders) {
            for (int i = maxFolders; i < folderList.size(); i++) {
                File folderToDelete = folderList.get(i);
                try {
                    FileUtils.deleteDirectory(folderToDelete);
                    success++;
                    logger.info("Deleted " + folderToDelete.getAbsolutePath());
                } catch (IOException e) {
                    Utils.errorHandler(e);
                }
            }
        } else logger.info("No deletion performed as available backups <=" + maxFolders);

        return success;
    }
}
