package uz.kundalik.telegram.payload.weather.forecast;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ForecastDayDTO {

    @JsonProperty("date")
    private String date;

    @JsonProperty("date_epoch")
    private long dateEpoch;

    @JsonProperty("day")
    private DayDTO day;

    @JsonProperty("astro")
    private AstroDTO astro;

    @JsonProperty("hour")
    private List<HourDTO> hour;
}
