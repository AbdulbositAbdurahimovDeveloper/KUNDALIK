package uz.kundalik.test.scraping;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import uz.kundalik.test.scraping.payload.BankCurrencyDTO;
import uz.kundalik.test.scraping.payload.BankRateDTO;
import uz.kundalik.test.scraping.payload.CommonUtil;
import uz.kundalik.test.scraping.payload.ExchangeRateDTO;
import uz.kundalik.test.scraping.template.BankNameScraper;
import uz.kundalik.test.scraping.template.MultiBankScaper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by: asrorbek
 * DateTime: 9/20/25 17:52
 **/

@Component
@RequiredArgsConstructor
public class MultiBankScraperImpl implements MultiBankScaper {

    private final BankNameScraper bankNameScraper;
    private final CommonUtil commonUtil;

    @Override
    public List<BankRateDTO> getAllBanksName() throws IOException {

        List<BankCurrencyDTO> bankCurrency = bankNameScraper.getBankCurrency();

        for (BankCurrencyDTO bankCurrencyDTO : bankCurrency) {

            if (bankCurrencyDTO.getCurrencyName().equals("EUR")) {

                return bankCurrencyDTO.getBankRateDTO();

            }

        }

        return new ArrayList<>();
    }

    @Override
    public List<ExchangeRateDTO> getBankExchangeRates(String bankUrl) throws IOException {

        List<ExchangeRateDTO> exchangeRates = new ArrayList<>();

        Document doc = Jsoup.connect(bankUrl).get();
        Elements rows = doc.select("div.col > div.row:has(div.col-2 a)");

        for (Element row : rows) {

            String code = Objects.requireNonNull(row.select("div.col-2 a span.medium-text").first()).text().trim();
            String currencyName = Objects.requireNonNull(row.select("div.col-2 a span.medium-text").last()).text().trim();
            String buyRate = Objects.requireNonNull(row.select("div.col-2").get(2).select("span.medium-text").last()).text().trim();
            String sellRate = Objects.requireNonNull(row.select("div.col-2").get(3).select("span.medium-text").last()).text().trim();
            String updatedAt = row.select("div.col-3 span.green-date").text().trim();

            ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();

            exchangeRateDTO.setCurrencyCode(code);
            exchangeRateDTO.setCurrencyName(currencyName);
            exchangeRateDTO.setBuyRate(commonUtil.parseDecimal(buyRate));
            exchangeRateDTO.setSellRate(commonUtil.parseDecimal(sellRate));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(updatedAt, formatter);

            exchangeRateDTO.setUpdatedAt(dateTime);

            exchangeRates.add(exchangeRateDTO);
        }

        return exchangeRates;

    }

}
