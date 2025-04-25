package daemon.Config;

import com.fasterxml.jackson.annotation.JsonFormat;
import daemon.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackupConfig extends BaseConfig implements Comparable<BackupConfig> {
    private String name;
    private String fromLocation;
    private String toLocation = Constants.DEFAULT_BACKUP_DIR;
    private BackupInterval interval = BackupInterval.ofDays(1);
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Kolkata")
    private Date lastTime = null;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private CompressionType compression = CompressionType.NONE;

    public Date nextTime() {
        return interval.after(lastTime);
    }

    @Override
    public int compareTo(BackupConfig other) {
        return (int) Duration.between(this.nextTime().toInstant(), other.nextTime().toInstant()).toMillis();
    }
}
