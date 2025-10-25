package uz.kundalik.test.scraping.payload;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by: asrorbek
 * DateTime: 9/20/25 19:01
 **/

@Component
public class CommonUtil {

    public BigDecimal parseDecimal(String value) {

        if (value == null) return null;

        String digits = value.replaceAll("[^0-9]", ""); // faqat raqamlar

        if (digits.isEmpty()) return null;

        return new BigDecimal(digits);

    }
}
