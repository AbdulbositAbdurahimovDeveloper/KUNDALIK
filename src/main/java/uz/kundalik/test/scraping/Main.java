package uz.kundalik.test.scraping;

import uz.kundalik.test.scraping.payload.BankCurrencyDTO;
import uz.kundalik.test.scraping.payload.CommonUtil;
import uz.kundalik.test.scraping.template.BankNameScraper;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        BankNameScraper bankNameScraper = new BankNameScraperImpl(new CommonUtil());
        List<BankCurrencyDTO> bankCurrency = bankNameScraper.getBankCurrency();
        System.out.println(bankCurrency);

    }
}
