package uz.kundalik.telegram.payload.currency;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO representing currency exchange rate information.
 * This class is used to map the JSON response from the Central Bank of Uzbekistan API.
 */
@Data
public class CurrencyRateDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("Code")
    private String code;

    @JsonProperty("Ccy")
    private String currency;

    @JsonProperty("CcyNm_RU")
    private String nameRu;

    @JsonProperty("CcyNm_UZ")
    private String nameUz;

    @JsonProperty("CcyNm_UZC")
    private String nameUzc;

    @JsonProperty("CcyNm_EN")
    private String nameEn;

    @JsonProperty("Nominal")
    private String nominal;

    @JsonProperty("Rate")
    private String rate;

    @JsonProperty("Diff")
    private String diff;

    @JsonProperty("Date")
    private String date;
}
