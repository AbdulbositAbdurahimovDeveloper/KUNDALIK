package uz.kundalik.telegram.payload.prayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO representing the Hijri (Islamic) date information.
 */
@Data
public class HijriDateDTO {

    @JsonProperty("month")
    private String month;

    @JsonProperty("day")
    private int day;
}
