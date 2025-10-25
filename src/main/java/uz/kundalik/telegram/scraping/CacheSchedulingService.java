package uz.kundalik.telegram.scraping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uz.kundalik.site.config.cache.CacheNames; // CacheNames klassingizni import qiling

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheSchedulingService {

    private final CacheManager cacheManager;

    /**
     * Har kuni yarim tunda (00:00:00) "prayer_day" keshini tozalaydi.
     * Cron ifodasi: "sekund minut soat kun oy hafta_kuni" formatida.
     * "0 0 0 * * *" -> Har kuni 00:00:00 da.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void evictPrayerDayCache() {
        log.info("Cron job started: Evicting '{}' cache.", CacheNames.PRAYER_DAY);
        try {
            Cache cache = cacheManager.getCache(CacheNames.PRAYER_DAY);
            if (cache != null) {
                cache.clear();
                log.info("'{}' cache has been cleared successfully.", CacheNames.PRAYER_DAY);
            } else {
                log.warn("Cache with name '{}' not found.", CacheNames.PRAYER_DAY);
            }
        } catch (Exception e) {
            log.error("Error occurred while evicting cache '{}'", CacheNames.PRAYER_DAY, e);
        }
    }
}