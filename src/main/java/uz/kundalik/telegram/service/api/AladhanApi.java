package uz.kundalik.telegram.service.api;

import uz.kundalik.telegram.payload.aladhan.PrayerData;

public interface AladhanApi {
    PrayerData getTimingsByCityAndDate(String date, String city, String country);
}
