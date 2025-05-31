package daemon.Backup.Compression;

import daemon.Utils;
import logger.LoggerUtil;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

public class GzipCompressor extends Compressor{
    private static final Logger logger = LoggerUtil.getLogger(GzipCompressor.class);

    public GzipCompressor(File input, File output) {
        super(input, output);
    }
    public GzipCompressor(Path input, Path output) {
        super(input, output);
    }

    @Override
    public void compress() {
        try (FileOutputStream fos = new FileOutputStream(output);
             GzipCompressorOutputStream gcos = new GzipCompressorOutputStream(fos);
             TarArchiveOutputStream tarOs = new TarArchiveOutputStream(gcos)) {

            // Enable support for long file names (POSIX mode)
            tarOs.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

            // Recursively add the file/folder to the archive.
            addFileToTarGz(tarOs, input, "");

            logger.info("Compression complete. Created: " + output);
        } catch (IOException e) {
            Utils.errorHandler(e);
        }
    }
    private void addFileToTarGz(TarArchiveOutputStream tOut, File file, String parent) throws IOException {
        // Build the entry name within the archive.
        String entryName = parent + file.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(file, entryName);
        tOut.putArchiveEntry(tarEntry);

        if (file.isFile()) {
            // If it's a file, write its data to the archive.
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[2048];
                int count;
                while ((count = fis.read(buffer)) != -1) tOut.write(buffer, 0, count);
            }
            tOut.closeArchiveEntry();
        } else {
            // For a directory, we need to finish the entry first
            tOut.closeArchiveEntry();
            // Then recursively add all files in the directory
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    // Ensure directories are separated by a slash
                    addFileToTarGz(tOut, child, entryName + "/");
                }
            }
        }
    }
}
