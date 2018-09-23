package com.fengd201.auth.common.cache;

/**
 * 
 * Class to manage cache
 * 
 * Cache strategy can be changed by calling changeCacheStrategy() method
 *
 */
public class CacheManager {
  private static CacheStrategy cacheMap = new LRUCache(500);

  public CacheManager() {
  };

  public static void putCache(Cacheable object) {
    cacheMap.put(object);
  }

  public static Cacheable getCache(Object identifier) {
    Cacheable object = (Cacheable) cacheMap.get(identifier);

    if (object == null)
      return null;
    if (object.isExpired()) {
      // remove from cache if expired
      cacheMap.remove(identifier);
      return null;
    } else {
      return object;
    }
  }

  public static boolean containsCache(Object identifier) {
    return cacheMap.contains(identifier);
  }

  public static boolean removeCache(Object identifier) {
    return cacheMap.remove(identifier);
  }

  public static void changeCacheStrategy(CacheStrategy strategy) {
    cacheMap = strategy;
  }
}
