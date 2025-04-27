package daemon;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import logger.LoggerUtil;

import java.util.Arrays;
import java.util.logging.Logger;

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
}
