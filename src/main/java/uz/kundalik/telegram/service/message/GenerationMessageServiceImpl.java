package uz.kundalik.telegram.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.kundalik.telegram.enums.UserStatus;
import uz.kundalik.telegram.payload.aladhan.DateInfo;
import uz.kundalik.telegram.payload.aladhan.PrayerData;
import uz.kundalik.telegram.payload.aladhan.Timings;
import uz.kundalik.telegram.payload.currency.CurrencyRateDTO;
import uz.kundalik.telegram.payload.prayer.HijriDateDTO;
import uz.kundalik.telegram.payload.prayer.PrayerDayDTO;
import uz.kundalik.telegram.payload.prayer.PrayerTimesDTO;
import uz.kundalik.telegram.payload.weather.WeatherResponseDTO;
import uz.kundalik.telegram.payload.weather.current.CurrentDTO;
import uz.kundalik.telegram.payload.weather.forecast.ForecastDayDTO;
import uz.kundalik.telegram.payload.weather.location.LocationDTO;
import uz.kundalik.telegram.utils.Utils;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static uz.kundalik.telegram.utils.Utils.*;

@Service
@RequiredArgsConstructor
public class GenerationMessageServiceImpl implements GenerationMessageService {

    private final i18n i18n;

    @Override
    public String weatherDayFormatter(WeatherResponseDTO weatherResponseDTO, String langCode) {
        LocationDTO location = weatherResponseDTO.getLocation();
        CurrentDTO current = weatherResponseDTO.getCurrent();
        ForecastDayDTO forecastDayDTO = weatherResponseDTO.getForecast().getForecastDay().get(0);

        return i18n.get(Utils.i18n.WEATHER_INFO, langCode).formatted(
                location.getName(),
                location.getLocaltime(),
                current.getTempC(),
                current.getFeelslikeC(),
                current.getWindDir(),
                current.getWindKph(),
                current.getHumidity(),
                current.getPressureMb(),
                forecastDayDTO.getDay().getMaxTempC(),
                forecastDayDTO.getDay().getMinTempC(),
                forecastDayDTO.getDay().getDailyChanceOfRain(),
                forecastDayDTO.getAstro().getSunrise(),
                forecastDayDTO.getAstro().getSunset()
        );
    }

    @Override
    public String prayerDayFormatter(PrayerDayDTO prayerDayDTO, String langCode) {
        if (prayerDayDTO == null) {
            return i18n.get(Utils.i18n.PRAYER_NOT_FOUND, langCode);
        }
        PrayerTimesDTO times = prayerDayDTO.getTimes();
        HijriDateDTO hijriDate = prayerDayDTO.getHijriDate();

        return i18n.get(Utils.i18n.PRAYER_TIME_HTML, langCode).formatted(
                prayerDayDTO.getRegion(),
                prayerDayDTO.getDate(),
                prayerDayDTO.getWeekday(),
                hijriDate.getMonth(),
                hijriDate.getDay(),
                times.getTongSaharlik(),
                times.getQuyosh(),
                times.getPeshin(),
                times.getAsr(),
                times.getShomIftor(),
                times.getHufton()
        );
    }

    @Override
    public String currencyFormatter(List<CurrencyRateDTO> currencyRateDTOS, Long chatId, String langCode, UserStatus userStatus) {
        if (currencyRateDTOS == null || currencyRateDTOS.isEmpty()) {
            return i18n.get(Utils.i18n.CURRENCY_INFO_NOT_FOUND, langCode);
        }

        if (userStatus.equals(UserStatus.ANONYMOUS)) {
            return formatForAnonymousUser(currencyRateDTOS, langCode);
        } else {
            // TODO: Boshqa foydalanuvchilar uchun
            return "Not implemented for registered users.";
        }
    }

    @Override
    public String formatForFreeUser(PrayerData prayerData, String langCode, String city) {
        Timings timings = prayerData.getTimings();
        DateInfo date = prayerData.getDate();

        // 1. Matn shablonini i18n'dan olamiz
        String template = i18n.get(Utils.i18n.PRAYER_TIMES_FREE_FORMAT, langCode);

        // 2. Shablonni ma'lumotlar bilan to'ldiramiz
        return template
                .replace("{city}", city)
                .replace("{gregorian_date}", date.getGregorian().getDay() + "-" + date.getGregorian().getMonth().getEn())
                .replace("{hijri_date}", date.getHijri().getDay() + "-" + date.getHijri().getMonth().getEn())
                .replace("{fajr}", timings.getFajr())
                .replace("{sunrise}", timings.getSunrise())
                .replace("{dhuhr}", timings.getDhuhr())
                .replace("{asr}", timings.getAsr())
                .replace("{maghrib}", timings.getMaghrib())
                .replace("{isha}", timings.getIsha());
    }

    /**
     * Namoz vaqtlarini premium foydalanuvchilar uchun i18n yordamida formatlaydi.
     *
     * @param prayerData Namoz vaqtlari va sanalarini o'z ichiga olgan obyekt.
     * @param langCode   Tarjima uchun til kodi.
     * @param city       Shahar nomi.
     * @param now        Joriy vaqt (qolgan vaqtni hisoblash uchun).
     * @return Dinamik formatlangan tayyor matn.
     */
    @Override
    public String formatForPremiumUser(PrayerData prayerData, String langCode, String city, LocalTime now) {
        Timings timings = prayerData.getTimings();
        DateInfo date = prayerData.getDate();

        // Matnni yig'ish uchun StringBuilder
        StringBuilder resultBuilder = new StringBuilder();

        // 1. Sarlavha (Header) qismini formatlash
        String headerTemplate = i18n.get(Utils.i18n.PRAYER_TIMES_PREMIUM_HEADER, langCode);
        String gregorianFullDate = String.format("%s-%s, %s, %s",
                date.getGregorian().getDay(), date.getGregorian().getMonth().getEn(),
                date.getGregorian().getYear(), date.getGregorian().getWeekday().getEn());
        String hijriFullDate = String.format("%s-%s (%s), %s",
                date.getHijri().getDay(), date.getHijri().getMonth().getEn(),
                date.getHijri().getMonth().getAr(), date.getHijri().getYear());

        resultBuilder.append(
                headerTemplate
                        .replace("{city}", city.toUpperCase())
                        .replace("{gregorian_full_date}", gregorianFullDate)
                        .replace("{hijri_full_date}", hijriFullDate)
        ).append("\n\n");

        // 2. Namoz vaqtlarini va ularning nomlarini tartiblangan Map'ga joylash
        Map<String, LocalTime> prayerTimes = new LinkedHashMap<>();
        prayerTimes.put("Fajr", LocalTime.parse(timings.getFajr()));
        prayerTimes.put("Dhuhr", LocalTime.parse(timings.getDhuhr()));
        prayerTimes.put("Asr", LocalTime.parse(timings.getAsr()));
        prayerTimes.put("Maghrib", LocalTime.parse(timings.getMaghrib()));
        prayerTimes.put("Isha", LocalTime.parse(timings.getIsha()));

        // 3. Keyingi namozni aniqlash
        String nextPrayerKey = null;
        for (Map.Entry<String, LocalTime> entry : prayerTimes.entrySet()) {
            if (entry.getValue().isAfter(now)) {
                nextPrayerKey = entry.getKey();
                break;
            }
        }

        // 4. Har bir namoz qatorini holatiga qarab formatlash
        for (Map.Entry<String, LocalTime> entry : prayerTimes.entrySet()) {
            String prayerKey = entry.getKey();
            LocalTime prayerTime = entry.getValue();
            String formattedTime = prayerTime.format(DateTimeFormatter.ofPattern("HH:mm"));

            String lineTemplate;
            String finalLine;

            String i18nPrayerName = getI18nPrayerName(prayerKey, langCode);

            if (prayerKey.equals(nextPrayerKey)) {
                // Bu KEYINGI namoz
                lineTemplate = i18n.get(Utils.i18n.PRAYER_TIMES_PREMIUM_LINE_NEXT, langCode);

                Duration duration = Duration.between(now, prayerTime);
                long hours = duration.toHours();
                long minutes = duration.toMinutes() % 60;

                String remainingTimeFormat = i18n.get(Utils.i18n.PRAYER_TIMES_PREMIUM_REMAINING_TIME_FORMAT, langCode);
                String remainingTime = remainingTimeFormat
                        .replace("{hours}", String.valueOf(hours))
                        .replace("{minutes}", String.valueOf(minutes));

                finalLine = lineTemplate
                        .replace("{prayer_name}", i18nPrayerName)
                        .replace("{prayer_time}", formattedTime)
                        .replace("{remaining_time}", remainingTime);

            } else if (prayerTime.isBefore(now)) {
                // Bu O'TIB KETGAN (o'qilgan) namoz
                lineTemplate = i18n.get(Utils.i18n.PRAYER_TIMES_PREMIUM_LINE_PAST, langCode);
                finalLine = lineTemplate
                        .replace("{prayer_name}", i18nPrayerName)
                        .replace("{prayer_time}", formattedTime);
            } else {
                // Bu KELAJAKDAGI (lekin keyingi bo'lmagan) namoz
                lineTemplate = i18n.get(Utils.i18n.PRAYER_TIMES_PREMIUM_LINE_FUTURE, langCode);
                finalLine = lineTemplate
                        .replace("{prayer_name}", i18nPrayerName)
                        .replace("{prayer_time}", formattedTime);
            }

            resultBuilder.append(finalLine).append("\n");
        }

        // 5. Oxirgi qism (Footer)
        String footerTemplate = i18n.get(Utils.i18n.PRAYER_TIMES_PREMIUM_FOOTER, langCode);
        resultBuilder.append(
                footerTemplate.replace("{sunrise}", timings.getSunrise())
        );

        return resultBuilder.toString();
    }

    /**
     * i18n'dan namozning tarjima qilingan nomini olish uchun yordamchi metod.
     * Masalan, "Fajr" -> i18n.get("prayer_name_fajr") -> "🏙 Bomdod"
     */
    private String getI18nPrayerName(String prayerKey, String langCode) {
        String i18nKey;
        switch (prayerKey.toLowerCase()) { // Kichik harflar bilan solishtirish ishonchliroq
            case "fajr":    i18nKey = Utils.i18n.PRAYER_NAME_FAJR; break;
            case "dhuhr":   i18nKey = Utils.i18n.PRAYER_NAME_DHUHR; break;
            case "asr":     i18nKey = Utils.i18n.PRAYER_NAME_ASR; break;
            case "maghrib": i18nKey = Utils.i18n.PRAYER_NAME_MAGHRIB; break;
            case "isha":    i18nKey = Utils.i18n.PRAYER_NAME_ISHA; break;
            default:        return prayerKey; // Agar noma'lum kalit kelsa, o'zini qaytaramiz
        }
        return i18n.get(i18nKey, langCode);
    }

    private String formatForAnonymousUser(List<CurrencyRateDTO> allRates, String langCode) {
        StringBuilder sb = new StringBuilder();

        // Shablonlarni i18n orqali olamiz
        String headerTemplate = i18n.get(Utils.i18n.CURRENCY_PRETTY_HEADER_HTML, langCode);
        String lineTemplate = i18n.get(Utils.i18n.CURRENCY_PRETTY_LINE_HTML, langCode);
        String footerTemplate = i18n.get(Utils.i18n.CURRENCY_PRETTY_FOOTER_HTML, langCode);

        // 1. Sarlavhani formatlaymiz
        String date = allRates.get(0).getDate();
        sb.append(String.format(headerTemplate, date));
        sb.append("\n\n"); // Sarlavha va kurslar orasida bo'sh qator

        // 2. Ko'rsatiladigan valyutalarni belgilaymiz
        Set<String> targetCurrencies = Set.of("USD", "EUR", "RUB");

        // 3. Har bir kerakli valyutani formatlab, qo'shamiz
        allRates.stream()
                .filter(rate -> targetCurrencies.contains(rate.getCurrency()))
                .forEach(rate -> {
                    // YORDAMCHI METODGA ENDI langCode HAM BERILADI
                    String formattedLine = formatSingleCurrency(rate, lineTemplate, langCode); // <-- O'ZGARISH
                    sb.append(formattedLine).append("\n");
                });

        // 4. Izoh (footer) qismini qo'shamiz
        sb.append(footerTemplate);

        return sb.toString();
    }

    /**
     * Bitta valyuta ma'lumotini shablon asosida formatlaydi.
     */
    private String formatSingleCurrency(CurrencyRateDTO rate, String lineTemplate, String langCode) {
        String flag = getFlagForCurrency(rate.getCurrency());
        String currencyName = getCurrencyNameByLang(rate, langCode);
        String formattedDiff = formatDifference(rate.getDiff());

        return String.format(lineTemplate,
                flag,
                rate.getNominal(),
                rate.getCurrency(),
                currencyName,
                rate.getRate(),
                formattedDiff
        );
    }

    /**
     * Kurs farqini (diff) o'sish/pasayish belgisi bilan formatlaydi.
     */
    private String formatDifference(String diff) {
        try {
            double diffValue = Double.parseDouble(diff);
            if (diffValue > 0) {
                return "🔺+" + diff;
            } else if (diffValue < 0) {
                return "🔻" + diff;
            } else {
                return "➖0.00";
            }
        } catch (NumberFormatException e) {
            return diff;
        }
    }

    private String getCurrencyNameByLang(CurrencyRateDTO rate, String langCode) {
        if (rate == null || langCode == null) return "";
        return switch (langCode.toLowerCase()) {
            case "ru" -> rate.getNameRu();
            case "en" -> rate.getNameEn();
            default -> rate.getNameUz();
        };
    }

    /**
     * Valyuta kodi bo'yicha CurrencyFlags interfeysidan mos bayroqni qaytaradi.
     */
    private String getFlagForCurrency(String currencyCode) {
        if (currencyCode == null) return CurrencyFlags.DEFAULT_FLAG;

        return switch (currencyCode) {
            case "USD" -> CurrencyFlags.USD;
            case "EUR" -> CurrencyFlags.EUR;
            case "RUB" -> CurrencyFlags.RUB;
            case "GBP" -> CurrencyFlags.GBP;
            case "JPY" -> CurrencyFlags.JPY;
            case "CHF" -> CurrencyFlags.CHF;
            case "CNY" -> CurrencyFlags.CNY;
            case "KZT" -> CurrencyFlags.KZT;
            case "TRY" -> CurrencyFlags.TRY;
            case "AED" -> CurrencyFlags.AED;
            case "AFN" -> CurrencyFlags.AFN;
            case "AMD" -> CurrencyFlags.AMD;
            case "ARS" -> CurrencyFlags.ARS;
            case "AUD" -> CurrencyFlags.AUD;
            case "AZN" -> CurrencyFlags.AZN;
            case "BDT" -> CurrencyFlags.BDT;
            case "BGN" -> CurrencyFlags.BGN;
            case "BHD" -> CurrencyFlags.BHD;
            case "BND" -> CurrencyFlags.BND;
            case "BRL" -> CurrencyFlags.BRL;
            case "BYN" -> CurrencyFlags.BYN;
            case "CAD" -> CurrencyFlags.CAD;
            case "CUP" -> CurrencyFlags.CUP;
            case "CZK" -> CurrencyFlags.CZK;
            case "DKK" -> CurrencyFlags.DKK;
            case "DZD" -> CurrencyFlags.DZD;
            case "EGP" -> CurrencyFlags.EGP;
            case "GEL" -> CurrencyFlags.GEL;
            case "HKD" -> CurrencyFlags.HKD;
            case "HUF" -> CurrencyFlags.HUF;
            case "IDR" -> CurrencyFlags.IDR;
            case "ILS" -> CurrencyFlags.ILS;
            case "INR" -> CurrencyFlags.INR;
            case "IQD" -> CurrencyFlags.IQD;
            case "IRR" -> CurrencyFlags.IRR;
            case "ISK" -> CurrencyFlags.ISK;
            case "JOD" -> CurrencyFlags.JOD;
            case "KGS" -> CurrencyFlags.KGS;
            case "KHR" -> CurrencyFlags.KHR;
            case "KRW" -> CurrencyFlags.KRW;
            case "KWD" -> CurrencyFlags.KWD;
            case "LAK" -> CurrencyFlags.LAK;
            case "LBP" -> CurrencyFlags.LBP;
            case "LYD" -> CurrencyFlags.LYD;
            case "MAD" -> CurrencyFlags.MAD;
            case "MDL" -> CurrencyFlags.MDL;
            case "MMK" -> CurrencyFlags.MMK;
            case "MNT" -> CurrencyFlags.MNT;
            case "MXN" -> CurrencyFlags.MXN;
            case "MYR" -> CurrencyFlags.MYR;
            case "NOK" -> CurrencyFlags.NOK;
            case "NZD" -> CurrencyFlags.NZD;
            case "OMR" -> CurrencyFlags.OMR;
            case "PHP" -> CurrencyFlags.PHP;
            case "PKR" -> CurrencyFlags.PKR;
            case "PLN" -> CurrencyFlags.PLN;
            case "QAR" -> CurrencyFlags.QAR;
            case "RON" -> CurrencyFlags.RON;
            case "RSD" -> CurrencyFlags.RSD;
            case "SAR" -> CurrencyFlags.SAR;
            case "SDG" -> CurrencyFlags.SDG;
            case "SEK" -> CurrencyFlags.SEK;
            case "SGD" -> CurrencyFlags.SGD;
            case "SYP" -> CurrencyFlags.SYP;
            case "THB" -> CurrencyFlags.THB;
            case "TJS" -> CurrencyFlags.TJS;
            case "TMT" -> CurrencyFlags.TMT;
            case "TND" -> CurrencyFlags.TND;
            case "UAH" -> CurrencyFlags.UAH;
            case "UYU" -> CurrencyFlags.UYU;
            case "VES" -> CurrencyFlags.VES;
            case "VND" -> CurrencyFlags.VND;
            case "XDR" -> CurrencyFlags.XDR;
            case "YER" -> CurrencyFlags.YER;
            case "ZAR" -> CurrencyFlags.ZAR;
            default -> CurrencyFlags.DEFAULT_FLAG;
        };
    }
}
