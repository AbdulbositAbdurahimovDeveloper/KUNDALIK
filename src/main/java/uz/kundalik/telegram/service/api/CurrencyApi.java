package uz.kundalik.telegram.service.api;

import uz.kundalik.telegram.payload.currency.CurrencyRateDTO;

import java.util.List;

/**
 * A service interface for fetching currency exchange rates.
 * <p>
 * This abstraction provides methods to retrieve currency data, hiding the
 * underlying implementation details (e.g., whether it's from a remote API or a local cache).
 *
 * @see CurrencyApiImpl for the default implementation.
 */
public interface CurrencyApi {

    /**
     * Retrieves a list of all current currency exchange rates from the data source.
     * <p>
     * The result of this method is typically cached to reduce load on the external API.
     *
     * @return A {@link List} of {@link CurrencyRateDTO} objects. Returns an empty list
     *         if the data cannot be fetched.
     */
    List<CurrencyRateDTO> getAllRates();

    /**
     * Retrieves a specific currency exchange rate by its alphabetic code (e.g., "USD", "EUR").
     * <p>
     * This method may leverage the result of {@link #getAllRates()} and is also cacheable.
     *
     * @param code The 3-letter currency code (case-insensitive).
     * @return The corresponding {@link CurrencyRateDTO}, or {@code null} if not found.
     */
    CurrencyRateDTO getByCode(String code);
}