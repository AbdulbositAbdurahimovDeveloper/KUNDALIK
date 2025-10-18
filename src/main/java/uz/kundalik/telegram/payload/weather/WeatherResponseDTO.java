package uz.kundalik.telegram.payload.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uz.kundalik.telegram.payload.weather.current.CurrentDTO;
import uz.kundalik.telegram.payload.weather.forecast.ForecastDTO;
import uz.kundalik.telegram.payload.weather.location.LocationDTO;

@Data
public class WeatherResponseDTO {

    @JsonProperty("location")
    private LocationDTO location;

    @JsonProperty("current")
    private CurrentDTO current;

    @JsonProperty("forecast")
    private ForecastDTO forecast;
}
