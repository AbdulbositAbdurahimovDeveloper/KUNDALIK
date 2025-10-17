// src/main/java/uz/kundalik/telegram/payload/weather/WeatherApiResponse.java
package uz.kundalik.telegram.payload.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // API dan kelishi mumkin bo'lgan bizga keraksiz maydonlarni e'tiborsiz qoldiradi
public class WeatherApiResponse {

//    @JsonProperty("location")
//    private LocationDto location;

    @JsonProperty("current")
    private CurrentWeatherDto current;

    @JsonProperty("forecast")
    private ForecastDto forecast;
}