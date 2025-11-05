package com.kuclubunion.openhousescoreboard_kuclubunion_2025.global;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.springframework.stereotype.Component;

@Component
public class LeaderboardCache {
  private final Map<String, Object> cache = new ConcurrentHashMap<>();

  public Object getOrElse(String key, Supplier<Object> loader) {
    return cache.computeIfAbsent(key, k -> loader.get());
  }

  public void put(String key, Object value) {
    cache.put(key, value);
  }

  public void evict(String key) {
    cache.remove(key);
  }
}
