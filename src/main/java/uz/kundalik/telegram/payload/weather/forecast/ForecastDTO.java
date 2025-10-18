package uz.kundalik.telegram.payload.weather.forecast;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ForecastDTO {

    @JsonProperty("forecastday")
    private List<ForecastDayDTO> forecastDay;
}
