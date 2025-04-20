package daemon.ConfigLoader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import daemon.Config.CoreConfig;
import daemon.Constants;
import daemon.MakeObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CoreConfigLoader implements IConfigLoader {
    private String configFile = Constants.DEFAULT_CORE_CONFIG_FILE;

    public CoreConfigLoader() {}
    public CoreConfigLoader(String configFile) throws Exception {
        if (!configFile.endsWith(".json")) throw new Exception(configFile + " does not end with `.json` extension!");

        // Check if the file exists in the classpath
        InputStream inJson = CoreConfig.class.getResourceAsStream(configFile);
        if (inJson == null) throw new Exception(configFile + " does not exist!");

        this.configFile = configFile;
    }

    @Override
    public CoreConfig load() throws IOException {
        ObjectMapper objectMapper = MakeObjectMapper.makeNew();
        InputStream inJson = CoreConfig.class.getResourceAsStream(configFile);

        if (inJson == null) {
            throw new IOException("Resource not found: " + configFile);
        }

        return objectMapper.readValue(inJson, new TypeReference<CoreConfig>() {});
    }
}
