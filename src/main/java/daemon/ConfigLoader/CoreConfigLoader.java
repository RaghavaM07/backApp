package daemon.ConfigLoader;

import com.fasterxml.jackson.databind.ObjectMapper;
import daemon.Config.CoreConfig;
import daemon.Constants;
import daemon.MakeObjectMapper;

import java.io.File;
import java.io.IOException;

public class CoreConfigLoader implements IConfigLoader {
    private String configFile = Constants.DEFAULT_CORE_CONFIG_FILE;

    public CoreConfigLoader() {}
    public CoreConfigLoader(String configFile) throws Exception {
        if (!configFile.endsWith(".json")) throw new Exception(configFile + " does not end with `.json` extension!");

        File f = new File(configFile);
        if(!f.exists() || f.isDirectory()) {
            throw new Exception(configFile + " does not exist!");
        }

        this.configFile = configFile;
    }

    @Override
    public CoreConfig load() throws IOException {
        ObjectMapper objectMapper = MakeObjectMapper.makeNew();
        File jsonFile = new File(configFile);

        return objectMapper.readValue(jsonFile, CoreConfig.class);
    }
}
