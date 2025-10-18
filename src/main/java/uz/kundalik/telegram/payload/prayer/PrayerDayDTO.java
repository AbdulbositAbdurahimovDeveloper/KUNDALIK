package uz.kundalik.telegram.payload.prayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO representing a single day's prayer times information from islomapi.uz.
 */
@Data
public class PrayerDayDTO {

    @JsonProperty("region")
    private String region;

    @JsonProperty("date")
    private String date;

    @JsonProperty("hijri_date")
    private HijriDateDTO hijriDate;

    @JsonProperty("weekday")
    private String weekday;

    @JsonProperty("times")
    private PrayerTimesDTO times;
}
