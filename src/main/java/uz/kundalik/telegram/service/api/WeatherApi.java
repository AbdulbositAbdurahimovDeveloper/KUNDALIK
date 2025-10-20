package uz.kundalik.telegram.service.api;

import uz.kundalik.telegram.payload.weather.WeatherResponseDTO;
import uz.kundalik.telegram.payload.weather.search.LocationResponseDTO;
import uz.kundalik.telegram.payload.weather.search.SearchLocationDTO;

import java.util.List;

/**
 * A service interface for fetching weather forecast data.
 * <p>
 * This abstraction supports fetching data by city name or geographic coordinates.
 * Implementations are responsible for interacting with the external weather provider,
 * handling API key management, and caching responses.
 *
 * @see WeatherApiImpl for the default implementation.
 */
public interface WeatherApi {

    /**
     * Fetches the weather forecast for a given location and number of days.
     * This is the primary, cacheable method that other methods delegate to.
     *
     * @param query The location query (e.g., "Tashkent", or "41.31,69.24").
     * @param days  The number of forecast days to retrieve.
     * @return A {@link WeatherResponseDTO} with the forecast data.
     */
    WeatherResponseDTO getWeatherForecast(String query, int days);

    /**
     * A convenience method to get the forecast for a city with a specific number of days.
     *
     * @param cityName The name of the city.
     * @param days The number of forecast days.
     * @return A {@link WeatherResponseDTO}.
     */
    WeatherResponseDTO info(String cityName, int days);

    /**
     * A convenience method to get the forecast for a city using the default number of days.
     *
     * @param cityName The name of the city.
     * @return A {@link WeatherResponseDTO}.
     */
    WeatherResponseDTO info(String cityName);

    /**
     * A convenience method to get the forecast for geographic coordinates with a specific number of days.
     *
     * @param latitude  The latitude.
     * @param longitude The longitude.
     * @param days The number of forecast days.
     * @return A {@link WeatherResponseDTO}.
     */
    WeatherResponseDTO info(Double latitude, Double longitude, int days);

    /**
     * A convenience method to get the forecast for geographic coordinates using the default number of days.
     *
     * @param latitude  The latitude.
     * @param longitude The longitude.
     * @return A {@link WeatherResponseDTO}.
     */
    WeatherResponseDTO info(Double latitude, Double longitude);


    List<SearchLocationDTO> search(String query);

    String dayFormatter(WeatherResponseDTO weatherResponseDTO, String langCode);
}