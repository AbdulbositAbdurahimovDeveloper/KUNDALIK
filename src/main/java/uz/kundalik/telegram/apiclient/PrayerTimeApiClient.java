package uz.kundalik.telegram.apiclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import uz.kundalik.telegram.payload.prayer.PrayerDayDTO;

import java.util.List;

/**
 * Feign client for the IslomAPI to fetch prayer times.
 * Base URL is configured via {@code application.prayer.base-url}.
 */
@FeignClient(name = "prayer-time-api", url = "${application.prayer.base-url}")
public interface PrayerTimeApiClient {

    /**
     * Fetches weekly prayer times. The endpoint path is configured via
     * {@code application.prayer.endpoints.week}.
     *
     * @param region The region name (e.g., "toshkent").
     * @return A list of {@link PrayerDayDTO} objects.
     */
    @GetMapping(value = "${application.prayer.endpoints.week}")
    List<PrayerDayDTO> getWeeklyTimes(@RequestParam("region") String region);

    /**
     * Fetches daily prayer times. The endpoint path is configured via
     * {@code application.prayer.endpoints.day}.
     *
     * @param region The region name (e.g., "toshkent").
     * @return A single {@link PrayerDayDTO} object.
     */
    @GetMapping(value = "${application.prayer.endpoints.day}")
    PrayerDayDTO getDailyTimes(@RequestParam("region") String region);
}