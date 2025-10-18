package uz.kundalik.site.exception;

/**
 * Custom exception type for handling all Weather API related errors.
 * <p>
 * This exception is thrown when:
 * <ul>
 *   <li>The remote weather API returns an error response (e.g. invalid API key, rate limit exceeded, city not found).</li>
 *   <li>There is a connection or timeout issue while calling the API.</li>
 *   <li>No valid API key is available to process the request.</li>
 * </ul>
 *
 * Typical usage example:
 * <pre>{@code
 * try {
 *     WeatherResponseDTO dto = weatherApi.info("Tashkent", 3);
 * } catch (WeatherApiException ex) {
 *     log.error("Weather API failed: {}", ex.getMessage());
 * }
 * }</pre>
 */
public class WeatherApiException extends RuntimeException {

    public WeatherApiException(String message) {
        super(message);
    }

    public WeatherApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
