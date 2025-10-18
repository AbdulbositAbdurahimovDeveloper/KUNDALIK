package uz.kundalik.telegram.service.api;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uz.kundalik.telegram.apiclient.CurrencyApiClient;
import uz.kundalik.telegram.payload.currency.CurrencyRateDTO;

import java.util.Collections;
import java.util.List;

/**
 * {@inheritDoc}
 * <p>
 * This implementation of {@link CurrencyApi} uses a {@link CurrencyApiClient} (Feign)
 * to fetch data from the Central Bank of Uzbekistan's API.
 * It includes robust caching and error handling mechanisms.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyApiImpl implements CurrencyApi {

    private final CurrencyApiClient currencyApiClient;

    /**
     * {@inheritDoc}
     * <p>
     * The result is cached under the "currencyAllRates" cache name. On a cache miss,
     * it calls the external API via the Feign client.
     */
    @Override
    @Cacheable("currencyAllRates")
    public List<CurrencyRateDTO> getAllRates() {
        try {
            log.info("Cache miss for all currency rates. Fetching from CBU API.");
            List<CurrencyRateDTO> rates = currencyApiClient.getAllRates();
            log.info("✅ Successfully loaded {} currency rates from CBU API.", rates.size());
            return rates;
        } catch (FeignException ex) {
            log.error("❌ Failed to fetch currency rates from CBU API. Status: {}, Body: {}", ex.status(), ex.contentUTF8(), ex);
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is cached separately under "currencyByCode". It relies on the
     * cached result of {@link #getAllRates()} to perform the filtering.
     */
    @Override
    @Cacheable(value = "currencyByCode", key = "#code.toUpperCase()")
    public CurrencyRateDTO getByCode(String code) {
        log.info("Cache miss for currency code: {}.", code);
        return getAllRates()
                .stream()
                .filter(rate -> rate.getCurrency().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }
}