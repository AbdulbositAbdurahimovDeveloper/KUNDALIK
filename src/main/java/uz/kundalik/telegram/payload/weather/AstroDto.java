// src/main/java/uz/kundalik/telegram/payload/weather/AstroDto.java
package uz.kundalik.telegram.payload.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AstroDto {

    @JsonProperty("sunrise")
    private String sunrise;
    @JsonProperty("sunset")
    private String sunset;
    @JsonProperty("moonrise")
    private String moonrise;
    @JsonProperty("moonset")
    private String moonset;
    @JsonProperty("moon_phase")
    private String moonPhase;
    @JsonProperty("moon_illumination")
    private int moonIllumination; // Qo'shildi
    @JsonProperty("is_moon_up")
    private int isMoonUp; // Qo'shildi
    @JsonProperty("is_sun_up")
    private int isSunUp; // Qo'shildi
}