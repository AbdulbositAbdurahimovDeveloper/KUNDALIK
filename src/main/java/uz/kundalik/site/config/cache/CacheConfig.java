package uz.kundalik.site.config.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    // BU METOD ENDI @Bean EMAS, PRIVATE HELPER METHOD!

    /**
     * Creates a specialized ObjectMapper instance specifically for Redis serialization.
     * This ObjectMapper is NOT exposed as a global bean to avoid interfering with
     * Spring MVC's default JSON handling for API requests.
     */
    private ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        return objectMapper;
    }

    // BU METOD HAM @Bean EMAS, chunki u faqat shu klassda ishlatiladi.
    private GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(redisObjectMapper());
    }

    @Bean
    @Primary
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = genericJackson2JsonRedisSerializer();

        // ... (bu qism o'zgarmaydi, faqat serializer'ni yuqoridagi metoddan oladi) ...
        final Duration userCacheTtl = Duration.ofMinutes(10);
        final Duration languageCacheTtl = Duration.ofHours(1);
        final Duration botCacheTtl = Duration.ofHours(1);
        final Duration weatherCacheTtl = Duration.ofMinutes(30);
        final Duration prayerCacheDayTtl = Duration.ofDays(1);
        final Duration prayerCacheWeekTtl = Duration.ofDays(7);

        Map<String, RedisCacheConfiguration> initialCacheConfigurations = new HashMap<>();
        initialCacheConfigurations.put(CacheNames.USERS, createCacheConfiguration(userCacheTtl, jsonRedisSerializer));
        initialCacheConfigurations.put(CacheNames.LANGUAGES, createCacheConfiguration(languageCacheTtl, jsonRedisSerializer));
        initialCacheConfigurations.put(CacheNames.TELEGRAM_BOT, createCacheConfiguration(botCacheTtl, jsonRedisSerializer));
        initialCacheConfigurations.put(CacheNames.WEATHER, createCacheConfiguration(weatherCacheTtl, jsonRedisSerializer));
        initialCacheConfigurations.put(CacheNames.PRAYER_DAY, createCacheConfiguration(prayerCacheDayTtl, jsonRedisSerializer));
        initialCacheConfigurations.put(CacheNames.PRAYER_WEEK, createCacheConfiguration(prayerCacheWeekTtl, jsonRedisSerializer));

        RedisCacheConfiguration defaultConfig = createCacheConfiguration(Duration.ofMinutes(5), jsonRedisSerializer);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(initialCacheConfigurations)
                .transactionAware()
                .build();
    }

    // Bu metod o'zgarmaydi
    private RedisCacheConfiguration createCacheConfiguration(Duration ttl, GenericJackson2JsonRedisSerializer serializer) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> cacheName + "::")
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        GenericJackson2JsonRedisSerializer jsonRedisSerializer = genericJackson2JsonRedisSerializer();
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }
}