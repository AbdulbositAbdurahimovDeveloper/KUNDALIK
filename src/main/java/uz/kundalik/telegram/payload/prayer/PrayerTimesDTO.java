package uz.kundalik.telegram.payload.prayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO representing the daily prayer times.
 */
@Data
public class PrayerTimesDTO {

    @JsonProperty("tong_saharlik")
    private String tongSaharlik;

    @JsonProperty("quyosh")
    private String quyosh;

    @JsonProperty("peshin")
    private String peshin;

    @JsonProperty("asr")
    private String asr;

    @JsonProperty("shom_iftor")
    private String shomIftor;

    @JsonProperty("hufton")
    private String hufton;
}
