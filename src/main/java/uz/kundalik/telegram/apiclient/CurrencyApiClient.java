package uz.kundalik.telegram.apiclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import uz.kundalik.telegram.payload.currency.CurrencyRateDTO;

import java.util.List;

/**
 * Feign client to fetch currency exchange rates from the Central Bank of Uzbekistan (CBU).
 * Base URL is configured via {@code application.currency.base-url}.
 */
@FeignClient(name = "cbu-api", url = "${application.currency.base-url}")
public interface CurrencyApiClient {

    /**
     * Fetches all current currency exchange rates.
     * Endpoint path is configured via {@code application.currency.endpoints.all-rates}.
     *
     * @return A list of {@link CurrencyRateDTO} objects.
     */
    @GetMapping(value = "${application.currency.endpoints.all-rates}")
    List<CurrencyRateDTO> getAllRates();
}