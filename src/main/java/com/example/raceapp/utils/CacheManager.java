package com.example.raceapp.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 * {@code CacheManager} is a simple in-memory cache implementation based on
 * {@link ConcurrentHashMap}. It provides methods to add, retrieve, and remove
 * cached data efficiently with optional Time-To-Live (TTL) support.
 *
 * <p>Intended for lightweight caching scenarios where a full-fledged caching
 * library may be excessive. The cache is not designed for distributed systems.</p>
 */
@Component
public class CacheManager {
    private static final Logger logger = Logger.getLogger(CacheManager.class.getName());
    private static final int MAX_CACHE_SIZE = 2;

    private final Map<String, CacheEntry> cacheMap = Collections.synchronizedMap(
            new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            }
    );
    private static final long DEFAULT_TTL = 3600_000; // 1 hour

    /**
     * Internal class to store cached value with expiration time.
     */
    private static class CacheEntry {
        final Object value;
        final long expireTime;

        CacheEntry(Object value, long ttl) {
            this.value = value;
            this.expireTime = System.currentTimeMillis() + ttl;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    /**
     * Adds an object to the cache with default TTL (1 hour).
     *
     * @param key   The unique identifier for the cached object.
     * @param value The object to be stored in the cache.
     */
    public void put(String key, Object value) {
        put(key, value, DEFAULT_TTL);
    }

    /**
     * Adds an object to the cache with custom TTL.
     *
     * @param key   The unique identifier for the cached object.
     * @param value The object to be stored in the cache.
     * @param ttl   Time-To-Live in milliseconds.
     */
    public void put(String key, Object value, long ttl) {
        if (ttl <= 0) {
            logger.warning("⚠️ [CACHE] Invalid TTL. Using default.");
            ttl = DEFAULT_TTL;
        }

        CacheEntry newEntry = new CacheEntry(value, ttl);
        CacheEntry oldEntry = cacheMap.put(key, newEntry);

        if (oldEntry != null) {
            logger.info("♻️ [CACHE] Existing entry updated");
        }
        logger.info("✅ [CACHE] New entry added");
    }

    /**
     * Retrieves an object from the cache with TTL validation.
     *
     * @param <T> The expected type of the cached object.
     * @param key The unique identifier for the cached object.
     * @return The cached object if found and not expired; otherwise, {@code null}.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        CacheEntry entry = cacheMap.get(key);

        if (entry == null) {
            logger.info("❌ [CACHE] Entry not found");
            return null;
        }

        if (entry.isExpired()) {
            cacheMap.remove(key);
            logger.info("⌛ [CACHE] Expired entry removed");
            return null;
        }

        logger.info("📌 [CACHE] Entry found");
        return (T) entry.value;
    }

    /**
     * Removes all cache entries whose keys match the specified pattern.
     *
     * @param pattern The prefix pattern used to identify keys for removal.
     */
    public void evictByKeyPattern(String pattern) {
        int initialSize = cacheMap.size();
        cacheMap.keySet().removeIf(key -> key.startsWith(pattern));
        int removed = initialSize - cacheMap.size();
        logger.log(Level.INFO, "🧹 [CACHE] Removed {0} entries", removed);
    }
}