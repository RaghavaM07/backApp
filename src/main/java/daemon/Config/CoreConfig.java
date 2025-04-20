package daemon.Config;

import com.fasterxml.jackson.annotation.JsonProperty;
import daemon.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreConfig extends BaseConfig {
    private String backupConfigFileLocation = Constants.DEFAULT_BACKUP_DIR;
    private boolean useSysTempAsFallback = true;
    private LogInfoConfig logInfo = defaultLogInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogInfoConfig {
        private String logLevel = "ERROR";
        private String logFileLocation = Constants.DEFAULT_LOG_FILE;
    }

    private static LogInfoConfig defaultLogInfo = new LogInfoConfig();
}
