package uz.kundalik.telegram.payload.aladhan;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AnnualCalendarResponse {
    private int code;
    private String status;
    private Map<String, List<PrayerData>> data;
}