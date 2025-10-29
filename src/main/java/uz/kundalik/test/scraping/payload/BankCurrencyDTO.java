package uz.kundalik.test.scraping.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by: asrorbek
 * DateTime: 9/20/25 17:38
 **/

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankCurrencyDTO {

    private String currencyName;

    private List<BankRateDTO> bankRateDTO;

}
