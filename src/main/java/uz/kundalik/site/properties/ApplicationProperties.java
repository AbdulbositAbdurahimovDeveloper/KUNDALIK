package uz.kundalik.site.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

    private SiteProperties site;
    private JwtProperties jwt;
    private MinioProperties minio;
    private TelegramProperties telegram;
    private EskizProperties eskiz;
    private WeatherProperties weather;
    private PrayerProperties prayer;
    private CurrencyProperties currency;
}
