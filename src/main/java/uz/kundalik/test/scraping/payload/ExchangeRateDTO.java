package uz.kundalik.test.scraping.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link ExchangeRate}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateDTO implements Serializable {

    private Long id;

    private String bankName;

    private String bankUrl;

    private String currencyCode;

    private String currencyName;

    private BigDecimal buyRate;

    private BigDecimal sellRate;

    private LocalDateTime updatedAt;

}