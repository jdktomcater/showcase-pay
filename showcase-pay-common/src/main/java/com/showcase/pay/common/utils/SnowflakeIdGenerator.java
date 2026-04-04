package com.showcase.pay.common.utils;

/**
 * Snowflake ID generator for generating globally unique IDs.
 *
 * <p>The ID structure (64 bits):
 * <pre>
 * 0 - 41-bit timestamp - 10-bit worker ID - 12-bit sequence number
 * </pre>
 *
 * <ul>
 *   <li>Sign bit: 1 bit (always 0)</li>
 *   <li>Timestamp: 41 bits (millisecond precision, supports ~69 years)</li>
 *   <li>Worker ID: 10 bits (supports up to 1024 nodes)</li>
 *   <li>Sequence number: 12 bits (supports 4096 IDs per millisecond per node)</li>
 * </ul>
 */
public class SnowflakeIdGenerator {

    /**
     * Start epoch (customizable, should be a time in the past)
     */
    private static final long EPOCH = 1704067200000L; // 2024-01-01 00:00:00 UTC

    /**
     * Number of bits for worker ID
     */
    private static final long WORKER_ID_BITS = 10L;

    /**
     * Number of bits for sequence number
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * Maximum worker ID (2^10 - 1 = 1023)
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * Maximum sequence number (2^12 - 1 = 4095)
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * Left shift bits for timestamp
     */
    private static final long TIMESTAMP_LEFT_SHIFT = WORKER_ID_BITS + SEQUENCE_BITS;

    /**
     * Left shift bits for worker ID
     */
    private static final long WORKER_ID_LEFT_SHIFT = SEQUENCE_BITS;

    /**
     * Worker ID (0-1023)
     */
    private final long workerId;

    /**
     * Sequence number (0-4095)
     */
    private long sequence = 0L;

    /**
     * Last timestamp (in milliseconds)
     */
    private long lastTimestamp = -1L;

    /**
     * Construct with default worker ID 0.
     */
    public SnowflakeIdGenerator() {
        this(0);
    }

    /**
     * Construct with specified worker ID.
     *
     * @param workerId the worker ID (0-1023)
     * @throws IllegalArgumentException if workerId is out of range
     */
    public SnowflakeIdGenerator(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("Worker ID must be between 0 and " + MAX_WORKER_ID);
        }
        this.workerId = workerId;
    }

    /**
     * Generate the next unique ID.
     *
     * @return a unique snowflake ID
     * @throws RuntimeException if clock moves backwards
     */
    public synchronized long nextId() {
        long timestamp = getCurrentTimestamp();

        // Clock moved backwards
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID for " +
                    (lastTimestamp - timestamp) + " milliseconds");
        }

        // Same millisecond, increment sequence
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // Sequence overflow, wait for next millisecond
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // New millisecond, reset sequence
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // Build the ID
        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (workerId << WORKER_ID_LEFT_SHIFT)
                | sequence;
    }

    /**
     * Wait until the next millisecond.
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    /**
     * Get current timestamp in milliseconds.
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * Parse a snowflake ID to extract the timestamp.
     *
     * @param id the snowflake ID
     * @return the timestamp in milliseconds
     */
    public static long parseTimestamp(long id) {
        return (id >> TIMESTAMP_LEFT_SHIFT) + EPOCH;
    }
}
