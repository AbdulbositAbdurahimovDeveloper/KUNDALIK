package uz.kundalik.telegram.payload.weather.forecast;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AstroDTO {

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
    private int moonIllumination;

    @JsonProperty("is_moon_up")
    private int isMoonUp;

    @JsonProperty("is_sun_up")
    private int isSunUp;
}
