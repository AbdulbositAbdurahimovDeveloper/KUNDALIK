package uz.kundalik.site.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.kundalik.site.payload.response.ResponseDTO;
import uz.kundalik.telegram.payload.prayer.PrayerDayDTO;
import uz.kundalik.telegram.payload.weather.WeatherResponseDTO;
import uz.kundalik.telegram.service.api.IslomApi;
import uz.kundalik.telegram.service.api.WeatherApi;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private final WeatherApi weatherApi;
    private final IslomApi islomApi;

    @GetMapping("/weather")
    public ResponseEntity<ResponseDTO<?>> testWeather(String city) {

        WeatherResponseDTO info = weatherApi.info(city);

        return ResponseEntity.ok(ResponseDTO.success(info, "Weather endpoint is working!"));
    }

    @GetMapping("/prayer")
    public ResponseEntity<ResponseDTO<?>> testPrayer(String region) {
        PrayerDayDTO todayPrayerTimes = islomApi.getTodayPrayerTimes(region);
        return ResponseEntity.ok(ResponseDTO.success(todayPrayerTimes, "Prayer endpoint is working!"));
    }
}
