package daemon;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import logger.LoggerUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class Utils {
    private static final Logger logger = LoggerUtil.getLogger(Utils.class);
    private static final String[] supportedExtns = {".json"};

    public static ObjectMapper makeNewObjectMapper() {
        logger.fine("Making new object mapper");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(MapperFeature.ALLOW_COERCION_OF_SCALARS);
        return objectMapper;
    }

    public static void errorHandler(Exception e) {
        logger.severe("===========================================");
        logger.severe("ERROR Occurred: " + e.getClass());
        logger.severe("===========================================");
        logger.severe(e.getMessage());
        logger.severe("===========================================");
        logger.severe(Arrays.toString(e.getStackTrace()));
    }

    public static boolean isFileSupported(String s) {
        return Arrays.stream(supportedExtns).anyMatch(s::endsWith);
    }

    public static synchronized long calculateChecksum(Path filePath) {
        CRC32 crc = new CRC32();

        try (InputStream fis = Files.newInputStream(filePath);
             CheckedInputStream cis = new CheckedInputStream(fis, crc)) {
            byte[] buffer = new byte[1024];

            while (cis.read(buffer) != -1) { /* Intentionally left blank for checked input stream */ }
        } catch (IOException e) {
            Utils.errorHandler(e);
        }
        return crc.getValue();
    }
}
