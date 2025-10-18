package uz.kundalik.telegram.service.api;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uz.kundalik.site.config.cache.CacheNames;
import uz.kundalik.telegram.apiclient.PrayerTimeApiClient;
import uz.kundalik.telegram.payload.prayer.PrayerDayDTO;

import java.util.Collections;
import java.util.List;

/**
 * {@inheritDoc}
 * <p>
 * This implementation uses the {@link PrayerTimeApiClient} (Feign) to fetch data from IslomAPI.uz.
 * It caches responses to improve performance and reduce API load.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IslomApiImpl implements IslomApi {

    private final PrayerTimeApiClient prayerTimeApiClient;

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(cacheNames = CacheNames.PRAYER_WEEK, key = "#region")
    public List<PrayerDayDTO> getWeeklyPrayerTimes(String region) {
        try {
            log.info("Cache miss for weekly prayer times: {}. Fetching from API.", region);
            return prayerTimeApiClient.getWeeklyTimes(region);
        } catch (FeignException ex) {
            log.error("Failed to fetch weekly prayer times for region '{}'. Status: {}, Body: {}",
                    region, ex.status(), ex.contentUTF8(), ex);
            return Collections.emptyList();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(cacheNames = CacheNames.PRAYER_DAY, key = "#region")
    public PrayerDayDTO getTodayPrayerTimes(String region) {
        try {
            log.info("Cache miss for daily prayer times: {}. Fetching from API.", region);
            return prayerTimeApiClient.getDailyTimes(region);
        } catch (FeignException ex) {
            log.error("Failed to fetch today's prayer times for region '{}'. Status: {}, Body: {}",
                    region, ex.status(), ex.contentUTF8(), ex);
            return null;
        }
    }
}