package daemon.ConfigLoader;

import daemon.Config.BaseConfig;

import java.io.IOException;

public interface IConfigLoader {
    public BaseConfig load() throws IOException;
}
