package daemon.ConfigLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import daemon.Config.CoreConfig;
import daemon.Constants;
import daemon.Utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class CoreConfigLoader implements IConfigLoader {
    private static final Logger logger = Logger.getLogger(CoreConfigLoader.class.getCanonicalName());
    private String configFile = Constants.DEFAULT_CORE_CONFIG_FILE;

    public CoreConfigLoader() {}
    public CoreConfigLoader(String configFile) throws Exception {
        if (!configFile.endsWith(".json")) {
            logger.severe(configFile + " is not a .json file");
            throw new Exception(configFile + " does not end with `.json` extension!");
        }

        File f = new File(configFile);
        if(!f.exists() || f.isDirectory()) {
            logger.severe(configFile + " DNE/is a directory");
            throw new Exception(configFile + " does not exist!");
        }

        this.configFile = configFile;
    }

    @Override
    public CoreConfig load() throws IOException {
        logger.fine("Loading CORE config from: " + configFile);
        ObjectMapper objectMapper = Utils.makeNew();
        File jsonFile = new File(configFile);

        CoreConfig retVal = objectMapper.readValue(jsonFile, CoreConfig.class);

        logger.info("Loaded core config successfully");
        logger.info(retVal.toString());

        return retVal;
    }
}
