package daemon.Config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Data
@NoArgsConstructor
public class BackupInterval {
    private int months = 0;
    private int days = 0;
    private int hours = 0;
    private int minutes = 0;

    public BackupInterval(int months, int days, int hours, int minutes) {
        setMonths(months);
        setDays(days);
        setHours(hours);
        setMinutes(minutes);
    }

    public Date after(Date time) {
        return Date.from(
                LocalDateTime.from(time.toInstant())
                        .plusMonths(months)
                        .plusDays(days)
                        .plusHours(hours)
                        .plusMinutes(minutes)
                        .atZone(ZoneId.systemDefault()).toInstant());
    }
    public Date afterNow() {
        return after(Date.from(Instant.now()));
    }

    public void setMonths(int months) {
        if (months < 0) throw new IllegalArgumentException("Months must be non-negative.");
        this.months = months;
    }
    public void setDays(int days) {
        if (days < 0) throw new IllegalArgumentException("Days must be non-negative.");
        this.days = days;
    }
    public void setHours(int hours) {
        if (hours < 0 || hours >= 24) throw new IllegalArgumentException("Hours must be between 0 and 23.");
        this.hours = hours;
    }
    public void setMinutes(int minutes) {
        if (minutes < 0 || minutes >= 60) throw new IllegalArgumentException("Minutes must be between 0 and 59.");
        this.minutes = minutes;
    }

    public static BackupInterval ofMonths(int months) {
        return new BackupInterval(months, 0, 0, 0);
    }
    public static BackupInterval ofDays(int days) {
        return new BackupInterval(0, days, 0, 0);
    }
    public static BackupInterval ofHours(int hours) {
        return new BackupInterval(0, 0, hours, 0);
    }
    public static BackupInterval ofMinutes(int minutes) {
        return new BackupInterval(0, 0, 0, minutes);
    }

    public long toMillis() {
        return 2_629_746_000L*months + 86_400_000L*days + 3_600_000L*hours + 60_000L*minutes;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        if(months > 0) str.append(months).append("M");
        if(days > 0) str.append(days).append("d");
        if(hours > 0) str.append(hours).append("h");
        if(minutes > 0) str.append(minutes).append("m");

        return str.toString();
    }
}
