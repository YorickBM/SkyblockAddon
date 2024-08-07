package yorickbm.skyblockaddon.util.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExpiringCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache;
    private final ScheduledExecutorService scheduler;
    private final Long expireAfterAccessMillis; // Nullable
    private final Integer maxSize; // Nullable

    private static class CacheEntry<V> {
        final V value;
        volatile long lastAccessTime;

        CacheEntry(V value, long lastAccessTime) {
            this.value = value;
            this.lastAccessTime = lastAccessTime;
        }
    }

    private ExpiringCache(Builder<K, V> builder) {
        this.cache = new ConcurrentHashMap<>();
        this.scheduler = builder.expireAfterAccessMillis != null
                ? Executors.newScheduledThreadPool(1)
                : null;
        this.expireAfterAccessMillis = builder.expireAfterAccessMillis;
        this.maxSize = builder.maxSize;

        if (this.scheduler != null) {
            startEvictionTask();
        }
    }

    private void startEvictionTask() {
        if (expireAfterAccessMillis != null) {
            scheduler.scheduleAtFixedRate(() -> {
                long now = System.currentTimeMillis();
                cache.forEach((key, entry) -> {
                    if (now - entry.lastAccessTime > expireAfterAccessMillis) {
                        cache.remove(key);
                    }
                });
            }, expireAfterAccessMillis, expireAfterAccessMillis, TimeUnit.MILLISECONDS);
        }
    }

    public V getIfPresent(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null) {
            entry.lastAccessTime = System.currentTimeMillis();
            return entry.value;
        }
        return null;
    }

    public void put(K key, V value) {
        if (maxSize != null && cache.size() >= maxSize) {
            evictOldest();
        }
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    private void evictOldest() {
        if (maxSize != null) {
            long oldestAccessTime = Long.MAX_VALUE;
            K oldestKey = null;

            for (Map.Entry<K, CacheEntry<V>> entry : cache.entrySet()) {
                if (entry.getValue().lastAccessTime < oldestAccessTime) {
                    oldestAccessTime = entry.getValue().lastAccessTime;
                    oldestKey = entry.getKey();
                }
            }

            if (oldestKey != null) {
                cache.remove(oldestKey);
            }
        }
    }

    public Map<K, V> asMap() {
        Map<K, V> result = new ConcurrentHashMap<>();
        long now = System.currentTimeMillis();
        cache.forEach((key, entry) -> {
            if (expireAfterAccessMillis == null || now - entry.lastAccessTime <= expireAfterAccessMillis) {
                result.put(key, entry.value);
            }
        });
        return result;
    }

    public void clear() {
        cache.clear();
    }

    public void shutdown() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    public static <K, V> Builder<K, V> newBuilder() {
        return new Builder<>();
    }

    public static class Builder<K, V> {
        private Long expireAfterAccessMillis; // Nullable
        private Integer maxSize; // Nullable

        public Builder<K, V> expireAfterAccess(long duration, TimeUnit unit) {
            this.expireAfterAccessMillis = unit.toMillis(duration);
            return this;
        }

        public Builder<K, V> maximumSize(int size) {
            this.maxSize = size;
            return this;
        }

        public ExpiringCache<K, V> build() {
            return new ExpiringCache<>(this);
        }
    }
}