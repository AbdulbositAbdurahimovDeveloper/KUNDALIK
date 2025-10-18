package uz.kundalik.telegram.service.api;

import uz.kundalik.telegram.payload.prayer.PrayerDayDTO;

import java.util.List;

/**
 * A service interface for fetching Islamic prayer times.
 * <p>
 * Provides methods to get daily and weekly prayer schedules for various regions.
 * Implementations are expected to handle caching to minimize external API calls.
 *
 * @see IslomApiImpl for the default implementation.
 */
public interface IslomApi {

    /**
     * Retrieves prayer times for the entire week for a given region.
     *
     * @param region The name of the region (e.g., "toshkent").
     * @return A {@link List} of {@link PrayerDayDTO} for the week. Returns an empty list on failure.
     */
    List<PrayerDayDTO> getWeeklyPrayerTimes(String region);

    /**
     * Retrieves prayer times for the current day for a given region.
     *
     * @param region The name of the region (e.g., "toshkent").
     * @return A single {@link PrayerDayDTO} for today, or {@code null} on failure.
     */
    PrayerDayDTO getTodayPrayerTimes(String region);
}