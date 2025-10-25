package uz.kundalik.test.scraping;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uz.kundalik.test.scraping.payload.BankCurrencyDTO;
import uz.kundalik.test.scraping.payload.BankRateDTO;
import uz.kundalik.test.scraping.payload.CommonUtil;
import uz.kundalik.test.scraping.template.BankNameScraper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: asrorbek
 * DateTime: 9/20/25 17:28
 **/

@Component
@RequiredArgsConstructor
public class BankNameScraperImpl implements BankNameScraper {

    private final CommonUtil commonUtil;

    @Value("${application.banks.urls.bank-currency-url}")
    private String bankUrl;

    @Override
    public List<BankCurrencyDTO> getBankCurrency() throws IOException {

        Document document = Jsoup.connect(bankUrl).userAgent("Mozilla/5.0")
                .timeout(10_000)
                .get();

        List<BankCurrencyDTO> result = new ArrayList<>();

        // Sahifadagi barcha tab panel divlari
        Elements tabs = document.select("div[role=tabpanel][id^=best_]"); // id="best_USD", "best_RUB", ...

        for (Element tab : tabs) {

            String id = tab.id(); // misol: best_USD
            String currency = id.replace("best_", "").toUpperCase();

            List<BankRateDTO> bankRateDTOS = parseCurrencyTab(tab);

            BankCurrencyDTO bankCurrencyDTO = new BankCurrencyDTO(currency, bankRateDTOS);

            result.add(bankCurrencyDTO);

        }

        return result;

    }

    @Override
    public List<BankRateDTO> parseCurrencyTab(Element tab) {
        List<BankRateDTO> result = new ArrayList<>();

        // Chap taraf (sotib olish)
        Element buyBlock = tab.selectFirst(".bc-inner-blocks-left");
        if (buyBlock != null) {
            Elements items = buyBlock.select(".bc-inner-block-left-texts");
            for (Element item : items) {
                BankRateDTO dto = parseItem(item, true);
                if (dto != null) result.add(dto);
            }
        }

        // O‘ng taraf (sotish)
        Element sellBlock = tab.selectFirst(".bc-inner-blocks-right");
        if (sellBlock != null) {
            Elements items = sellBlock.select(".bc-inner-block-left-texts");
            for (Element item : items) {
                BankRateDTO dto = parseItem(item, false);
                if (dto != null) {
                    // Agar bank allaqachon bor bo‘lsa — buyRate yangilash o‘rniga sellRate set qilamiz
                    BankRateDTO existing = result.stream()
                            .filter(r -> r.bankName().equals(dto.bankName()))
                            .findFirst()
                            .orElse(null);
                    if (existing != null) {
                        result.remove(existing);
                        result.add(new BankRateDTO(
                                existing.bankName(),
                                existing.bankUrl(),
                                existing.buyRate(),
                                dto.sellRate()
                        ));
                    } else {
                        result.add(dto);
                    }
                }
            }
        }

        return result;
    }

    private BankRateDTO parseItem(Element item, boolean isBuy) {

        Element bankEl = item.selectFirst(".bc-inner-block-left-text a");
        if (bankEl == null) return null;

        String bankName = bankEl.text().trim();
        String bankUrl = bankEl.attr("href");

        Element rateEl = item.selectFirst("span.green-date");
        BigDecimal rate = null;
        if (rateEl != null) {
            rate = commonUtil.parseDecimal(rateEl.text());
        }

        return isBuy
                ? new BankRateDTO(bankName, bankUrl, rate, null)
                : new BankRateDTO(bankName, bankUrl, null, rate);
    }

}