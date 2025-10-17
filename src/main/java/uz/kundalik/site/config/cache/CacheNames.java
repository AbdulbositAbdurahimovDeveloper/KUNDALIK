package uz.kundalik.site.config.cache;

/**
 * A central place to define all cache names used in the application.
 * Using constants helps prevent typos and makes it easy to manage cache configurations.
 * All cache names are prefixed with "orom_::" to create a namespace in Redis
 * and avoid key collisions if the Redis instance is shared.
 */
public final class CacheNames {

    private static final String PREFIX = "kundalik_";

    /**
     * Cache for storing user-related data, like UserDTOs, keyed by username or ID.
     * TTL: 10 minutes.
     */
    public static final String USERS = PREFIX + "users";

    /**
     * Cache for storing Telegram bot messages (translations).
     * The key would be language code + message key (e.g., "uz_GREETING"),
     * and the value would be the translated message.
     * TTL: 24 hours (translations rarely change).
     */
    public static final String LANGUAGES = PREFIX + "languages";

    /**
     * Cache for temporary Telegram user states or data during multi-step processes.
     * For example, storing data while a user is filling out a booking form in the bot.
     * Keyed by chat_id.
     * TTL: 15 minutes.
     */
    public static final String TELEGRAM_BOT = PREFIX + "telegram_bot";

    // You can add more caches here in the future
    // public static final String ESTABLISHMENTS = PREFIX + "establishments";

    private CacheNames() {
        // Private constructor to prevent instantiation
    }
}