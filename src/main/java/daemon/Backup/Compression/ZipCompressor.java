package daemon.Backup.Compression;

import daemon.Utils;
import logger.LoggerUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipCompressor extends Compressor {
    private static final Logger logger = LoggerUtil.getLogger(ZipCompressor.class);

    public ZipCompressor(File input, File output) {
        super(input, output);
    }
    public ZipCompressor(Path input, Path output) {
        super(input, output);
    }

    @Override
    public void compress() {
        try (FileOutputStream fos = new FileOutputStream(output);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            // Start the recursive compression
            _zipFile(input, input.getName(), zos);
            logger.info("Compression complete. Created: " + output);
        } catch (IOException e) {
            Utils.errorHandler(e);
        }
    }
    private void _zipFile(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        // If fileToZip is a folder, we create an entry for the folder and recurse
        if (fileToZip.isDirectory()) {
            // Folder name should end with `/`
            if (!fileName.endsWith("/"))  fileName += "/";
            zos.putNextEntry(new ZipEntry(fileName));
            zos.closeEntry();

            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    _zipFile(childFile, fileName + childFile.getName(), zos);
                }
            }
            return;
        }

        // Otherwise, fileToZip is a file. Read its bytes and create a ZipEntry.
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[2048];
            int length;

            while ((length = fis.read(bytes)) >= 0)  zos.write(bytes, 0, length);
        }
    }
}
