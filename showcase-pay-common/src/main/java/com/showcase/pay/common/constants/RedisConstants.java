package com.showcase.pay.common.constants;

/**
 * Redis key constants for the payment system.
 */
public final class RedisConstants {

    private RedisConstants() {
    }

    /**
     * Key prefix separator
     */
    public static final String KEY_PREFIX = "showcase:pay:";

    // ==================== Order related keys ====================

    /**
     * Order info cache key pattern: showcase:pay:order:info:{orderId}
     */
    public static final String ORDER_INFO_KEY = KEY_PREFIX + "order:info:";

    /**
     * Order status cache key pattern: showcase:pay:order:status:{orderId}
     */
    public static final String ORDER_STATUS_KEY = KEY_PREFIX + "order:status:";

    /**
     * User orders list key pattern: showcase:pay:order:user:{userId}
     */
    public static final String USER_ORDERS_KEY = KEY_PREFIX + "order:user:";

    // ==================== Payment related keys ====================

    /**
     * Payment info cache key pattern: showcase:pay:payment:info:{paymentId}
     */
    public static final String PAYMENT_INFO_KEY = KEY_PREFIX + "payment:info:";

    /**
     * Payment status cache key pattern: showcase:pay:payment:status:{paymentId}
     */
    public static final String PAYMENT_STATUS_KEY = KEY_PREFIX + "payment:status:";

    /**
     * Payment lock key pattern: showcase:pay:payment:lock:{paymentId}
     */
    public static final String PAYMENT_LOCK_KEY = KEY_PREFIX + "payment:lock:";

    // ==================== Distributed lock keys ====================

    /**
     * Distributed lock key pattern: showcase:pay:lock:{businessKey}
     */
    public static final String LOCK_KEY = KEY_PREFIX + "lock:";

    /**
     * Idempotency key pattern: showcase:pay:idempotent:{bizNo}
     */
    public static final String IDEMPOTENT_KEY = KEY_PREFIX + "idempotent:";

    // ==================== Rate limiting keys ====================

    /**
     * Rate limit key pattern: showcase:pay:ratelimit:{userId}:{api}
     */
    public static final String RATE_LIMIT_KEY = KEY_PREFIX + "ratelimit:";

    // ==================== TTL values (in seconds) ====================

    /**
     * Default TTL: 30 minutes
     */
    public static final long DEFAULT_TTL = 1800;

    /**
     * Order info TTL: 2 hours
     */
    public static final long ORDER_INFO_TTL = 7200;

    /**
     * Payment info TTL: 1 hour
     */
    public static final long PAYMENT_INFO_TTL = 3600;

    /**
     * Distributed lock TTL: 30 seconds
     */
    public static final long LOCK_TTL = 30;

    /**
     * Idempotency key TTL: 24 hours
     */
    public static final long IDEMPOTENT_TTL = 86400;
}
