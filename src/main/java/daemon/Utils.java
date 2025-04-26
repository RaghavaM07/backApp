package daemon;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.logging.Logger;

public class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class.getCanonicalName());

    public static ObjectMapper makeNew() {
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
        System.exit(1);
    }
}
