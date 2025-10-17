// src/main/java/uz/kundalik/telegram/payload/weather/ForecastDayDto.java
package uz.kundalik.telegram.payload.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastDayDto {

    @JsonProperty("date")
    private String date;

    @JsonProperty("date_epoch")
    private long dateEpoch;

    @JsonProperty("day")
    private DayDto day;

    @JsonProperty("astro")
    private AstroDto astro;

    @JsonProperty("hour")
    private List<HourDto> hour;
}