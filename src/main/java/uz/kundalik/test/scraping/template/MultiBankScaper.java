package uz.kundalik.test.scraping.template;


import uz.kundalik.test.scraping.payload.BankRateDTO;
import uz.kundalik.test.scraping.payload.ExchangeRateDTO;

import java.io.IOException;
import java.util.List;

/**
 * Created by: asrorbek
 * DateTime: 9/20/25 17:52
 **/

public interface MultiBankScaper {

    List<BankRateDTO> getAllBanksName() throws IOException;

    List<ExchangeRateDTO> getBankExchangeRates(String bankUrl) throws IOException;
}
