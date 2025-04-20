package daemon.ConfigLoader;

import com.fasterxml.jackson.core.type.TypeReference;
import daemon.Config.BackupConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import daemon.Config.CoreConfig;
import daemon.MakeObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JsonConfigLoader implements IConfigLoader{
    private final String configFile;

    public JsonConfigLoader(String configFile) throws Exception {
        if(!configFile.endsWith(".json")) throw new Exception(configFile + " does not end with `.json` extension!");

        InputStream inJson = BackupConfig.class.getResourceAsStream(configFile);
        if (inJson == null) throw new Exception(configFile + " does not exist!");

        this.configFile = configFile;
    }

    @Override
    public BackupConfig load() throws IOException {
        ObjectMapper objectMapper = MakeObjectMapper.makeNew();
        InputStream inJson = BackupConfig.class.getResourceAsStream(configFile);

        if (inJson == null)  throw new IOException("Resource not found: " + configFile);

        return objectMapper.readValue(inJson, new TypeReference<BackupConfig>() {});
    }
}
