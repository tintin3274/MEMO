package th.ku.memo.model;

import android.os.Build;
import androidx.annotation.RequiresApi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Day implements Serializable {
    private int id;
    private String name;
    private String detail;
    private String colorBackground;
    private String colorText;
    private String calculate;
    private LocalDate date;
    private LocalDateTime timeCreate;
    private LocalDateTime timeUpdate;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getCalculationTime() {
        long between;
        switch (calculate) {
            case "D-DAY":
                String s = "D";
                between = ChronoUnit.DAYS.between(date, LocalDate.now());
                if(between > 0) s += "+" + between;
                else if(between == 0) s += "-DAY";
                else s += between;
                return s;
            case"DAYS":
                between = ChronoUnit.DAYS.between(date, LocalDate.now());
                if(between >= 0) between += 1;
                return between + " days";
            case"WEEKS":
                between = ChronoUnit.WEEKS.between(date, LocalDate.now());
                if(between == 0) return "This week";
                else if(between > 0) between += 1;
                return between + " weeks";
            default:
        }
        return "Unknown";
    }
}
