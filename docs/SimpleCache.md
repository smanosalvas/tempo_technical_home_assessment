## Code Review

You are reviewing the following code submitted as part of a task to implement an item cache in a highly concurrent
application. The anticipated load includes: thousands of reads per second, hundreds of writes per second, tens of
concurrent threads.
Your objective is to identify and explain the issues in the implementation that must be addressed before deploying the
code to production. Please provide a clear explanation of each issue and its potential impact on production behaviour.

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ReentrantLock;

public class SimpleCache<K, V>
{
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMs = 60000; // 1 minute
    private final ReentrantLock lock = new ReentrantLock();

    public static class CacheEntry<V>
    {
        private final V value;
        private final long timestamp;

        public CacheEntry(V value, long timestamp)
        {
            this.value = value;
            this.timestamp = timestamp;
        }

        public V getValue()
        {
            return value;
        }

        public long getTimestamp()
        {
            return timestamp;
        }
    }

    public void put(K key, V value)
    {
        lock.lock();
        try {
            cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
        } finally {
            lock.unlock();
        }
        
    }

    public V get(K key)
    {
        // lock.lock();

        // try {
        //     CacheEntry<V> entry = cache.get(key);
        //     if (entry != null)
        //     {
        //         if (System.currentTimeMillis() - entry.getTimestamp() < ttlMs)
        //         {
        //             return entry.getValue();
        //         }
        //     }
        //     return null;
        // } finally {
        //     lock.unlock();
        // }

        CacheEntry<V> entry = cache.compute(key, (k, currentValue) -> {
            if(entry == null || isExpired(currentValue)) {
                return null;
            }
            return currentValue;
        });
        
        return entry.value;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - entry.getTimestamp() >= ttlMs;
    }

    public int size()
    {
        return cache.size();
    }
}
```

## Code review output

1. **Expired entries are never cleaned up**: `SimpleCache` does not provide a cleanup method, cleanup strategy, or
   eviction policy. This is a critical production issue because the `put` method can keep adding elements to the cache,
   and memory usage can grow over time. There is a high risk of exhausting host resources and potentially crashing the
   application in production.

2. **Cache stampede / thundering herd risk**: The current API only exposes `get` and `put`, so the cache cannot coordinate loading. A stronger design would provide
   loading semantics, for example `getOrLoad(K key, Function<K, V> loader)`, and use per-key atomic operations such as
   `ConcurrentHashMap.compute(...)` to ensure only one reload happens for a key at a time. Consider: 
   ```java
      public V getOrLoad(K key, Function<K, V> loader) {
       CacheEntry<V> entry = cache.compute(key, (k, existing) -> {
           if (existing != null && !isExpired(existing)) {
               return existing;
           }
   
           V loadedValue = loader.apply(k);
           return new CacheEntry<>(loadedValue, System.nanoTime());
       });
   
       return entry.value();
      }
   ```
   Be careful when using ConcurrentHashMap.computeIfAbsent(...) for TTL-aware caching. An entry may still exist in the
   map but be logically expired. In that case, the key is not absent, so the computeIfAbsent mapping function would not
   run, and the expired entry would not be refreshed.

3. **Time source for timestamp**: `System.currentTimeMillis()` uses wall-clock time. This can be a potential problem
   because the system clock is controlled at the operating-system level and may be adjusted by NTP or changed manually,
   accidentally, or intentionally. This can affect expiration validation by making the TTL effectively longer for a
   particular entry or causing entries to expire too early. It is recommended to use monotonic time for elapsed-time
   measurement, since it is designed to measure durations and only moves forward. `System.nanoTime()` would be safer for
   TTL validation.

4. **Null handling ambiguity**: `ConcurrentHashMap` prohibits null keys and null values. However, this cache can still
   allow a cached null value through `cache.put(key, new CacheEntry<>(null, timestamp))`, because the map value itself
   is a `CacheEntry` object. In that case, the `get` method may return `null`, but the consumer cannot determine whether
   the result means a cache miss, an expired entry, or an intentionally cached null value. If this behavior is
   intentional, it must be clearly documented. Otherwise, it may lead to production issues such as unexpected consumer
   behavior, incorrect key evaluation, and unnecessary calls to the source of truth.

5. **Capacity control**: Related to the eviction policy, the cache does not define a real maximum size. Setting an
   initial capacity for `ConcurrentHashMap` would not limit growth; it would only affect the initial internal
   allocation. In production, a maximum-size policy is important because it prevents the cache from growing indefinitely
   due to cold keys, stale entries, or high-cardinality usage patterns.

6. **Fixed TTL**: The TTL in milliseconds is hardcoded, which makes the class inflexible. This should probably be
   configurable with a default value. A configurable TTL allows faster operational adjustments without requiring a new
   deployment, maintenance window, or temporary service downtime.

7. **Improve observability**: This is not a functional issue, but the class lacks observability. It does not provide
   metrics such as hit/miss counts, expired counts, eviction counts, cache size trends, or latency metrics. This may not
   block a basic release, but it limits future debugging, tuning, and production monitoring. The impact may not be
   immediate, but it can slow down incident diagnosis and make performance tuning harder.

## Recommendation 

For production usage, I would recommend replacing this custom cache implementation with a mature caching library such as
Caffeine or Guava, rather than continuing to extend the current implementation manually. These libraries already provide
common production features such as TTL expiration, max-size eviction, loading semantics, refresh strategies, concurrency
optimizations, and cache statistics.

Example using Caffeine:

```java
   LoadingCache<K, V> cache = Caffeine.newBuilder()
   .maximumSize(100_000)
   .expireAfterWrite(Duration.ofMinutes(1))
   .recordStats()
   .build(key -> loadFromSource(key));
```

This would address several concerns in the current implementation: unbounded growth, hardcoded TTL behavior, missing
eviction strategy, cache stampede risk, and lack of observability. Caffeine would be my first choice for a modern Java
in-memory cache; Guava Cache is also a valid alternative, especially in projects that already depend on Guava.