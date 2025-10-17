// src/main/java/uz/kundalik/telegram/payload/weather/DayDto.java
package uz.kundalik.telegram.payload.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DayDto {

    @JsonProperty("maxtemp_c")
    private double maxTempC;
    @JsonProperty("maxtemp_f")
    private double maxTempF; // Qo'shildi
    @JsonProperty("mintemp_c")
    private double minTempC;
    @JsonProperty("mintemp_f")
    private double minTempF; // Qo'shildi
    @JsonProperty("avgtemp_c")
    private double avgTempC;
    @JsonProperty("avgtemp_f")
    private double avgTempF; // Qo'shildi
    @JsonProperty("maxwind_mph")
    private double maxWindMph; // Qo'shildi
    @JsonProperty("maxwind_kph")
    private double maxWindKph;
    @JsonProperty("totalprecip_mm")
    private double totalPrecipMm;
    @JsonProperty("totalprecip_in")
    private double totalPrecipIn; // Qo'shildi
    @JsonProperty("totalsnow_cm")
    private double totalSnowCm; // Qo'shildi
    @JsonProperty("avgvis_km")
    private double avgVisKm; // Qo'shildi
    @JsonProperty("avgvis_miles")
    private double avgVisMiles; // Qo'shildi
    @JsonProperty("avghumidity")
    private double avgHumidity;
    @JsonProperty("daily_will_it_rain")
    private int dailyWillItRain;
    @JsonProperty("daily_chance_of_rain")
    private int dailyChanceOfRain;
    @JsonProperty("daily_will_it_snow")
    private int dailyWillItSnow; // Qo'shildi
    @JsonProperty("daily_chance_of_snow")
    private int dailyChanceOfSnow; // Qo'shildi
    @JsonProperty("condition")
    private ConditionDto condition;
    @JsonProperty("uv")
    private double uv;
}