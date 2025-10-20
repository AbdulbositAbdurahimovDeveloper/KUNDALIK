package uz.kundalik.telegram.payload.weather.search;

import lombok.Data;

@Data
public class SearchLocationDTO {
    private Long id;
    private String name;
    private String region;
    private String country;
    private Double lat;
    private Double lon;
    private String url;
}
