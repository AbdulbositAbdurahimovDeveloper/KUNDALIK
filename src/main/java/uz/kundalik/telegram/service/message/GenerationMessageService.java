package uz.kundalik.telegram.service.message;

import uz.kundalik.telegram.enums.UserStatus;
import uz.kundalik.telegram.payload.aladhan.PrayerData;
import uz.kundalik.telegram.payload.currency.CurrencyRateDTO;
import uz.kundalik.telegram.payload.prayer.PrayerDayDTO;
import uz.kundalik.telegram.payload.weather.WeatherResponseDTO;

import java.time.LocalTime;
import java.util.List;

public interface GenerationMessageService {
    String weatherDayFormatter(WeatherResponseDTO weatherResponseDTO, String langCode);

    String prayerDayFormatter(PrayerDayDTO todayPrayerTimes, String langCode);

    String currencyFormatter(List<CurrencyRateDTO> currencyRateDTOS, Long chatId, String langCode, UserStatus userStatus);

    String formatForFreeUser(PrayerData prayerData, String langCode, String city);

    String formatForPremiumUser(PrayerData prayerData, String langCode, String city, LocalTime now);
}
