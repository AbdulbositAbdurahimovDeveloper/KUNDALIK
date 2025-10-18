package uz.kundalik.telegram.payload.weather.location;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LocationDTO {

    @JsonProperty("name")
    private String name;

    @JsonProperty("region")
    private String region;

    @JsonProperty("country")
    private String country;

    @JsonProperty("lat")
    private double lat;

    @JsonProperty("lon")
    private double lon;

    @JsonProperty("tz_id")
    private String tzId;

    @JsonProperty("localtime_epoch")
    private long localtimeEpoch;

    @JsonProperty("localtime")
    private String localtime;
}
