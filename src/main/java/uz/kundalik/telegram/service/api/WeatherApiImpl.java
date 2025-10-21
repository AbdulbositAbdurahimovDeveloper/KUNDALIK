package uz.kundalik.telegram.service.api;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uz.kundalik.site.config.cache.CacheNames;
import uz.kundalik.site.exception.WeatherApiException;
import uz.kundalik.site.properties.ApplicationProperties;
import uz.kundalik.site.properties.WeatherProperties;
import uz.kundalik.telegram.apiclient.WeatherApiClient;
import uz.kundalik.telegram.payload.weather.WeatherResponseDTO;
import uz.kundalik.telegram.payload.weather.current.CurrentDTO;
import uz.kundalik.telegram.payload.weather.forecast.ForecastDayDTO;
import uz.kundalik.telegram.payload.weather.location.LocationDTO;
import uz.kundalik.telegram.payload.weather.search.LocationResponseDTO;
import uz.kundalik.telegram.payload.weather.search.SearchLocationDTO;
import uz.kundalik.telegram.service.message.i18n;
import uz.kundalik.telegram.utils.Utils;

import java.util.List;

/**
 * {@inheritDoc}
 * <p>
 * This implementation uses the {@link WeatherApiClient} (Feign) to fetch weather data.
 * It features a robust API key rotation mechanism to handle rate limits and key failures.
 * All public-facing methods are routed through a single cacheable entry point
 * to ensure efficient data retrieval.
 */
@Slf4j
@Service
public class WeatherApiImpl implements WeatherApi {

    private final WeatherApiClient weatherApiClient;
    private final WeatherProperties weatherProperties;
    private final i18n i18n;
    private int currentKeyIndex = 0;

    // Constructor Injection is handled by Spring, but we write it for clarity if not using Lombok.
    public WeatherApiImpl(WeatherApiClient weatherApiClient, ApplicationProperties applicationProperties, i18n i18n) {
        this.weatherApiClient = weatherApiClient;
        this.weatherProperties = applicationProperties.getWeather();
        this.i18n = i18n;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This is the sole cacheable entry point for this service. All other {@code info}
     * methods delegate to this one. This design avoids Spring AOP self-invocation issues.
     */
    @Override
    @Cacheable(cacheNames = CacheNames.WEATHER, key = "#query + '_' + #days")
    public WeatherResponseDTO getWeatherForecast(String query, int days) {
        log.info("Cache miss for weather query: {}_{}. Fetching from external API.", query, days);
        return fetchFromApiWithKeyRotation(query, days);
    }

    @Override
    public WeatherResponseDTO info(String cityName, int days) {
        return getWeatherForecast(cityName, days);
    }

    @Override
    public WeatherResponseDTO info(String cityName) {
        return getWeatherForecast(cityName, weatherProperties.getDefaultDays());
    }

    @Override
    public WeatherResponseDTO info(Double latitude, Double longitude, int days) {
        String query = String.format("%s,%s", latitude, longitude);
        return getWeatherForecast(query, days);
    }

    @Override
    public WeatherResponseDTO info(Double latitude, Double longitude) {
        String query = String.format("%s,%s", latitude, longitude);
        return getWeatherForecast(query, weatherProperties.getDefaultDays());
    }

    @Override
    @Cacheable(cacheNames = CacheNames.WEATHER_SEARCH, key = "#query")
    public List<SearchLocationDTO> search(String query) {
        return locationResponseDTO(query);
    }

    /**
     * Executes the actual API call using the Feign client and handles the key rotation logic.
     * This method is kept private and is only invoked on a cache miss.
     *
     * @param query The location query.
     * @param days  The number of forecast days.
     * @return The fetched {@link WeatherResponseDTO}.
     * @throws WeatherApiException if all API keys are exhausted or a non-recoverable error occurs.
     */
    private WeatherResponseDTO fetchFromApiWithKeyRotation(String query, int days) {
        List<String> keys = weatherProperties.getApiKeys();
        if (keys == null || keys.isEmpty()) {
            throw new WeatherApiException("No Weather API keys have been configured.");
        }

        int initialKeyIndex = currentKeyIndex;
        for (int i = 0; i < keys.size(); i++) {
            String apiKey = keys.get(currentKeyIndex);
            try {
                log.debug("Attempting to fetch weather with API key at index {}.", currentKeyIndex);
                return weatherApiClient.getForecast(apiKey, query, days, "no", "no");
            } catch (FeignException ex) {
                log.warn("Weather API call failed with key at index {}. Status: {}. Rotating to next key.",
                        currentKeyIndex, ex.status());
                // Rotate to the next key
                currentKeyIndex = (currentKeyIndex + 1) % keys.size();
                // If we've tried all keys and returned to the start, break the loop
                if (currentKeyIndex == initialKeyIndex) break;
            } catch (Exception e) {
                // For non-Feign exceptions (e.g., network issues)
                throw new WeatherApiException("An unexpected error occurred while calling the Weather API: " + e.getMessage(), e);
            }
        }

        // If the loop completes without returning, all keys have failed.
        throw new WeatherApiException("All configured Weather API keys failed or are exhausted.");
    }

    private List<SearchLocationDTO> locationResponseDTO(String query) {
        List<String> keys = weatherProperties.getApiKeys();
        if (keys == null || keys.isEmpty()) {
            throw new WeatherApiException("No Weather API keys have been configured.");
        }

        int initialKeyIndex = currentKeyIndex;
        for (int i = 0; i < keys.size(); i++) {
            String apiKey = keys.get(currentKeyIndex);
            try {
                log.debug("Attempting to fetch weather with API key at index {}.", currentKeyIndex);
                return weatherApiClient.searchLocation(apiKey, query);
            } catch (FeignException ex) {
                log.warn("Weather API call failed with key at index {}. Status: {}. Rotating to next key.",
                        currentKeyIndex, ex.status());
                // Rotate to the next key
                currentKeyIndex = (currentKeyIndex + 1) % keys.size();
                // If we've tried all keys and returned to the start, break the loop
                if (currentKeyIndex == initialKeyIndex) break;
            } catch (Exception e) {
                // For non-Feign exceptions (e.g., network issues)
                throw new WeatherApiException("An unexpected error occurred while calling the Weather API: " + e.getMessage(), e);
            }
        }

        // If the loop completes without returning, all keys have failed.
        throw new WeatherApiException("All configured Weather API keys failed or are exhausted.");
    }
}