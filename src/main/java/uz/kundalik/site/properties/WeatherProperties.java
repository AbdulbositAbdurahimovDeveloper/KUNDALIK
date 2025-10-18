package uz.kundalik.site.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "application.weather")
public class WeatherProperties {
    private List<String> apiKeys;
    private String baseUrl;
    private Integer defaultDays;
    private String defaultCity;
}
