package uz.kundalik.telegram.service.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.kundalik.telegram.apiclient.AladhanApiClient;
import uz.kundalik.telegram.payload.aladhan.PrayerData;
import uz.kundalik.telegram.payload.aladhan.PrayerTimeResponse;

@Service
@RequiredArgsConstructor
public class AladhanApiImpl implements AladhanApi {

    private final AladhanApiClient aladhanApiClient;

    @Override
    public PrayerData getTimingsByCityAndDate(String date, String city, String country) {

        PrayerTimeResponse timingsByCityAndDate = aladhanApiClient.getTimingsByCityAndDate(date, city, country, 2);
        return timingsByCityAndDate.getData();

    }


}
