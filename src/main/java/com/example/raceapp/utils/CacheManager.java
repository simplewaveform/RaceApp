package com.example.raceapp.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 * CacheManager is a simple in-memory cache implementation based on ConcurrentHashMap.
 */
@Component
public class CacheManager {

    private static final Logger logger = Logger.getLogger(CacheManager.class.getName());
    private final ConcurrentHashMap<String, Object> cacheMap = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
        cacheMap.put(key, value);
        logger.info("✅ [CACHE PUT] Key: " + key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        T value = (T) cacheMap.get(key);
        if (value != null) {
            logger.info("📌 [CACHE HIT] Key: " + key);
        } else {
            logger.info("❌ [CACHE MISS] Key: " + key);
        }
        return value;
    }

    public void evictByKeyPattern(String pattern) {
        cacheMap.keySet().removeIf(key -> key.startsWith(pattern));
        logger.info("🧹 [CACHE EVICT] Pattern: " + pattern);
    }

    public void clearAll() {
        cacheMap.clear();
        logger.info("🚮 [CACHE CLEAR ALL]");
    }
}
