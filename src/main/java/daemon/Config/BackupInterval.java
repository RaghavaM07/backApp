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
}
