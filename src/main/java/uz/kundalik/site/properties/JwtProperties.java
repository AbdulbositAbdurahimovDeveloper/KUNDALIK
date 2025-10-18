package uz.kundalik.site.properties;

import lombok.Data;

import java.time.Duration;

@Data
public class JwtProperties {
    private String secret;
    private Duration accessTokenExpiration;
    private Duration refreshTokenExpiration;
}