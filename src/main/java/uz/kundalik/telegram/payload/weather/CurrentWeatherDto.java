// src/main/java/uz/kundalik/telegram/payload/weather/CurrentWeatherDto.java
package uz.kundalik.telegram.payload.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeatherDto {

    @JsonProperty("last_updated_epoch")
    private long lastUpdatedEpoch;
    @JsonProperty("last_updated")
    private String lastUpdated;
    @JsonProperty("temp_c")
    private double tempC;
    @JsonProperty("temp_f")
    private double tempF; // Qo'shildi
    @JsonProperty("is_day")
    private int isDay;
    @JsonProperty("condition")
    private ConditionDto condition;
    @JsonProperty("wind_mph")
    private double windMph; // Qo'shildi
    @JsonProperty("wind_kph")
    private double windKph;
    @JsonProperty("wind_degree")
    private int windDegree; // Qo'shildi
    @JsonProperty("wind_dir")
    private String windDir; // Qo'shildi
    @JsonProperty("pressure_mb")
    private double pressureMb;
    @JsonProperty("pressure_in")
    private double pressureIn; // Qo'shildi
    @JsonProperty("precip_mm")
    private double precipMm;
    @JsonProperty("precip_in")
    private double precipIn; // Qo'shildi
    @JsonProperty("humidity")
    private int humidity;
    @JsonProperty("cloud")
    private int cloud;
    @JsonProperty("feelslike_c")
    private double feelslikeC;
    @JsonProperty("feelslike_f")
    private double feelslikeF; // Qo'shildi
    @JsonProperty("windchill_c")
    private double windchillC; // Qo'shildi
    @JsonProperty("windchill_f")
    private double windchillF; // Qo'shildi
    @JsonProperty("heatindex_c")
    private double heatindexC; // Qo'shildi
    @JsonProperty("heatindex_f")
    private double heatindexF; // Qo'shildi
    @JsonProperty("dewpoint_c")
    private double dewpointC; // Qo'shildi
    @JsonProperty("dewpoint_f")
    private double dewpointF; // Qo'shildi
    @JsonProperty("vis_km")
    private double visKm;
    @JsonProperty("vis_miles")
    private double visMiles; // Qo'shildi
    @JsonProperty("uv")
    private double uv;
    @JsonProperty("gust_mph")
    private double gustMph; // Qo'shildi
    @JsonProperty("gust_kph")
    private double gustKph;
}