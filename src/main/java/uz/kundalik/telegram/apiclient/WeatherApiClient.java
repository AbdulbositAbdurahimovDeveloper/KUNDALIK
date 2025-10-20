package uz.kundalik.telegram.apiclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uz.kundalik.telegram.payload.weather.WeatherResponseDTO;
import uz.kundalik.telegram.payload.weather.search.LocationResponseDTO;
import uz.kundalik.telegram.payload.weather.search.SearchLocationDTO;

import java.util.List;

/**
 * A Feign client for interacting with the external Weather API.
 * <p>
 * This declarative client handles the underlying HTTP requests for fetching
 * weather forecast data. Configuration, including the base URL, is managed
 * in {@code application.yaml} under the {@code api-clients.weather} key.
 *
 * @see <a href="https://www.weatherapi.com/docs/">WeatherAPI Documentation</a>
 */
@FeignClient(name = "weather-api", url = "${application.weather.base-url}")
public interface WeatherApiClient {

    /**
     * Fetches the weather forecast for a specified location.
     * <p>
     * The request maps to a GET request like:
     * {@code /?key=<apiKey>&q=<query>&days=<days>&aqi=no&alerts=no}
     *
     * @param apiKey The API key for authentication.
     * @param query The location query, which can be a city name (e.g., "Tashkent") or lat/long (e.g., "41.31,69.24").
     * @param days The number of forecast days to retrieve (e.g., 1, 3).
     * @return A {@link WeatherResponseDTO} containing the forecast data.
     * @throws feign.FeignException if the API call fails (e.g., network error, 4xx/5xx responses).
     */
    @GetMapping(value = "${application.weather.endpoints.forecast}")
    WeatherResponseDTO getForecast(
            @RequestParam("key") String apiKey,
            @RequestParam("q") String query,
            @RequestParam("days") int days,
            @RequestParam(value = "aqi", defaultValue = "no") String aqi,
            @RequestParam(value = "alerts", defaultValue = "no") String alerts
    );

    @GetMapping(value = "?aqi=no&alerts=no")
    WeatherResponseDTO getForecast(
            @RequestParam("key") String apiKey,
            @RequestParam("q") String query,
            @RequestParam("days") int days
    );

    @GetMapping(value = "${application.weather.endpoints.search}")
    List<SearchLocationDTO> searchLocation(
            @RequestParam("key") String apiKey,
            @RequestParam("q") String query
    );
}