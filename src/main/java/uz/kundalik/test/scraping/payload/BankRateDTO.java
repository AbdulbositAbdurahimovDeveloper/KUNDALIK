package uz.kundalik.test.scraping.payload;

import java.math.BigDecimal;

/**
 * Created by: asrorbek
 * DateTime: 9/20/25 17:29
 **/


public record BankRateDTO(String bankName, String bankUrl, BigDecimal buyRate, BigDecimal sellRate) {
}
