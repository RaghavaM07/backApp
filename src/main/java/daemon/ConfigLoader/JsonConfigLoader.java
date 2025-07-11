package daemon.ConfigLoader;

import daemon.Config.BackupConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import daemon.Utils;
import logger.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class JsonConfigLoader implements IConfigLoader{
    private static final Logger logger = LoggerUtil.getLogger(JsonConfigLoader.class);
    private final String configFile;

    public JsonConfigLoader(String configFile) throws Exception {
        if(!configFile.endsWith(".json")) {
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
    public BackupConfig load() throws IOException {
        logger.fine("Loading config from: " + configFile);
        ObjectMapper objectMapper = Utils.makeNewObjectMapper();
        File jsonFile = new File(configFile);

        return objectMapper.readValue(jsonFile, BackupConfig.class);
    }
}
