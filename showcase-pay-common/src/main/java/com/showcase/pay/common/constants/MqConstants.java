package com.showcase.pay.common.constants;

/**
 * RocketMQ topic and tag constants for the payment system.
 */
public final class MqConstants {

    private MqConstants() {
    }

    // ==================== Topics ====================

    /**
     * Order events topic
     */
    public static final String TOPIC_ORDER = "ORDER_EVENT_TOPIC";

    /**
     * Payment events topic
     */
    public static final String TOPIC_PAYMENT = "PAYMENT_EVENT_TOPIC";

    /**
     * Notification events topic
     */
    public static final String TOPIC_NOTIFICATION = "NOTIFICATION_EVENT_TOPIC";

    // ==================== Tags ====================

    /**
     * Order created tag
     */
    public static final String TAG_ORDER_CREATED = "ORDER_CREATED";

    /**
     * Order cancelled tag
     */
    public static final String TAG_ORDER_CANCELLED = "ORDER_CANCELLED";

    /**
     * Order paid tag
     */
    public static final String TAG_ORDER_PAID = "ORDER_PAID";

    /**
     * Payment success tag
     */
    public static final String TAG_PAYMENT_SUCCESS = "PAYMENT_SUCCESS";

    /**
     * Payment failed tag
     */
    public static final String TAG_PAYMENT_FAILED = "PAYMENT_FAILED";

    /**
     * Payment timeout tag
     */
    public static final String TAG_PAYMENT_TIMEOUT = "PAYMENT_TIMEOUT";

    /**
     * Notification email tag
     */
    public static final String TAG_NOTIFY_EMAIL = "NOTIFY_EMAIL";

    /**
     * Notification SMS tag
     */
    public static final String TAG_NOTIFY_SMS = "NOTIFY_SMS";

    // ==================== Consumer Groups ====================

    /**
     * Order service consumer group
     */
    public static final String CG_ORDER = "cg_order_service";

    /**
     * Payment service consumer group
     */
    public static final String CG_PAYMENT = "cg_payment_service";

    /**
     * Notification service consumer group
     */
    public static final String CG_NOTIFICATION = "cg_notification_service";
}
