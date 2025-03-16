package com.example.raceapp.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 * {@code CacheManager} is a simple in-memory cache implementation based on
 * {@link ConcurrentHashMap}. It provides methods to add, retrieve, and remove
 * cached data efficiently.
 *
 * <p>Intended for lightweight caching scenarios where a full-fledged caching
 * library may be excessive. The cache is not designed for distributed systems.</p>
 */
@Component
public class CacheManager {
    private static final Logger logger = Logger.getLogger(CacheManager.class.getName());
    private final ConcurrentHashMap<String, Object> cacheMap = new ConcurrentHashMap<>();

    /**
     * Adds an object to the cache with the specified key.
     *
     * @param key   The unique identifier for the cached object.
     * @param value The object to be stored in the cache.
     */
    public void put(String key, Object value) {
        cacheMap.put(key, value);
        // Avoid logging sensitive or user-controlled data directly.
        logger.info("✅ [CACHE PUT] Key added to cache.");
    }

    /**
     * Retrieves an object from the cache based on the provided key.
     *
     * @param <T> The expected type of the cached object.
     * @param key The unique identifier for the cached object.
     * @return The cached object if found; otherwise, {@code null}.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        T value = (T) cacheMap.get(key);
        if (value != null) {
            // Avoid logging the key and only log the result without revealing sensitive data.
            logger.info("📌 [CACHE HIT] Cache contains a value for the given key.");
        } else {
            logger.info("❌ [CACHE MISS] No value found for the given key.");
        }
        return value;
    }

    /**
     * Removes all cache entries whose keys match the specified pattern.
     *
     * @param pattern The prefix pattern used to identify keys for removal.
     *                For example, passing {@code "USER_"} will remove all
     *                cache entries starting with "USER_".
     */
    public void evictByKeyPattern(String pattern) {
        cacheMap.keySet().removeIf(key -> key.startsWith(pattern));
        logger.info("🧹 [CACHE EVICT] Cache entries removed for the given pattern.");
    }
}
