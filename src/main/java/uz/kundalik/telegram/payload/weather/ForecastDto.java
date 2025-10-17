// src/main/java/uz/kundalik/telegram/payload/weather/ForecastDto.java
package uz.kundalik.telegram.payload.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastDto {

    @JsonProperty("forecastday")
    private List<ForecastDayDto> forecastDay;
}