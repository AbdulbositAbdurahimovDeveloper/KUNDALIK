package uz.kundalik.test.scraping.template;

import org.jsoup.nodes.Element;
import uz.kundalik.test.scraping.payload.BankCurrencyDTO;
import uz.kundalik.test.scraping.payload.BankRateDTO;

import java.io.IOException;
import java.util.List;

/**
 * Created by: asrorbek
 * DateTime: 9/20/25 17:28
 **/


public interface BankNameScraper {

    List<BankCurrencyDTO> getBankCurrency() throws IOException;

    List<BankRateDTO> parseCurrencyTab(Element element);

}
