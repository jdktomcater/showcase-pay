package com.showcase.pay.common.utils;

import cn.hutool.core.util.StrUtil;
import com.showcase.pay.common.constants.RedisConstants;
import com.showcase.pay.common.exception.BusinessException;
import com.showcase.pay.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis utility class providing convenient methods for Redis operations.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    // ==================== Common operations ====================

    /**
     * Set expiration time for a key.
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }

    /**
     * Get expiration time for a key in seconds.
     */
    public long getExpire(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null ? ttl : -1;
    }

    /**
     * Check if a key exists.
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Delete one or more keys.
     */
    public boolean delete(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                return Boolean.TRUE.equals(redisTemplate.delete(keys[0]));
            }
            return Boolean.TRUE.equals(redisTemplate.delete(List.of(keys)));
        }
        return false;
    }

    // ==================== String operations ====================

    /**
     * Get value by key.
     */
    public Object get(String key) {
        return key != null ? redisTemplate.opsForValue().get(key) : null;
    }

    /**
     * Set value for a key with default TTL.
     */
    public void set(String key, Object value) {
        set(key, value, RedisConstants.DEFAULT_TTL, TimeUnit.SECONDS);
    }

    /**
     * Set value for a key with specified TTL.
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * Set value only if the key does not exist (atomic operation).
     */
    public boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit));
    }

    /**
     * Increment value by delta.
     */
    public long increment(String key, long delta) {
        return Long.parseLong(String.valueOf(redisTemplate.opsForValue().increment(key, delta)));
    }

    /**
     * Decrement value by delta.
     */
    public long decrement(String key, long delta) {
        return increment(key, -delta);
    }

    // ==================== Hash operations ====================

    /**
     * Get value from a hash.
     */
    public Object hashGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * Get all entries from a hash.
     */
    public Map<Object, Object> hashGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * Set multiple fields in a hash.
     */
    public void hashPut(String key, Map<String, ?> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * Set a single field in a hash.
     */
    public boolean hashPut(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
        return true;
    }

    /**
     * Delete one or more fields from a hash.
     */
    public void hashDelete(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, (Object[]) fields);
    }

    /**
     * Check if a field exists in a hash.
     */
    public boolean hashHasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    // ==================== Set operations ====================

    /**
     * Add values to a set.
     */
    public long setAdd(String key, Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        return count != null ? count : 0;
    }

    /**
     * Get all members of a set.
     */
    public Set<Object> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * Check if a value is a member of a set.
     */
    public boolean setIsMember(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    /**
     * Remove values from a set.
     */
    public long setRemove(String key, Object... values) {
        Long count = redisTemplate.opsForSet().remove(key, values);
        return count != null ? count : 0;
    }

    // ==================== List operations ====================

    /**
     * Get a range of elements from a list.
     */
    public List<Object> listGet(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * Get all elements from a list.
     */
    public List<Object> listGetAll(String key) {
        return listGet(key, 0, -1);
    }

    /**
     * Get the size of a list.
     */
    public long listSize(String key) {
        Long size = redisTemplate.opsForList().size(key);
        return size != null ? size : 0;
    }

    /**
     * Get an element from a list by index.
     */
    public Object listIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * Append a value to the end of a list.
     */
    public long listRightPush(String key, Object value) {
        Long result = redisTemplate.opsForList().rightPush(key, value);
        return result != null ? result : 0;
    }

    /**
     * Remove and get the first element from a list.
     */
    public Object listLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * Remove and get the last element from a list.
     */
    public Object listRightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    // ==================== Distributed lock ====================

    /**
     * Acquire a distributed lock.
     *
     * @param lockKey  the lock key
     * @param clientId the client identifier (used for releasing the lock)
     * @param timeout  the lock timeout
     * @param unit     the time unit
     * @return true if lock acquired, false otherwise
     */
    public boolean lock(String lockKey, String clientId, long timeout, TimeUnit unit) {
        return setIfAbsent(lockKey, clientId, timeout, unit);
    }

    /**
     * Release a distributed lock (only if the client ID matches).
     *
     * @param lockKey  the lock key
     * @param clientId the client identifier
     * @return true if lock released, false otherwise
     */
    public boolean unlock(String lockKey, String clientId) {
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long result = redisTemplate.execute(
                    new org.springframework.data.redis.core.script.DefaultRedisScript<>(script, Long.class),
                    List.of(lockKey),
                    clientId
            );
            return result != null && result > 0;
        } catch (Exception e) {
            log.error("Failed to release distributed lock: {}", lockKey, e);
            return false;
        }
    }

    // ==================== Idempotency check ====================

    /**
     * Check and set idempotency key atomically.
     *
     * @param bizNo the business number
     * @return true if this is the first request (not duplicate), false if duplicate
     */
    public boolean checkIdempotent(String bizNo) {
        if (StrUtil.isBlank(bizNo)) {
            throw new BusinessException(ErrorCode.PARAM_VALIDATION_FAILED, "Business number is required");
        }
        String key = RedisConstants.IDEMPOTENT_KEY + bizNo;
        return setIfAbsent(key, "1", RedisConstants.IDEMPOTENT_TTL, TimeUnit.SECONDS);
    }

    // ==================== Batch operations ====================

    /**
     * Get values for multiple keys.
     */
    public List<Object> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * Set values for multiple key-value pairs.
     */
    public void multiSet(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }
}
