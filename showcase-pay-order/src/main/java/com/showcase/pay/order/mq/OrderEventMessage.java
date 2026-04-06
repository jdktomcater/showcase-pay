package com.showcase.pay.order.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Order Event Message
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Event type: CREATED, CANCELLED, PAID, STATUS_CHANGED
     */
    private String eventType;

    /**
     * Order number
     */
    private String orderNo;

    /**
     * User ID
     */
    private Long userId;

    /**
     * Order amount
     */
    private BigDecimal amount;

    /**
     * Order status
     */
    private String status;

    /**
     * Event timestamp
     */
    private Long timestamp;

    /**
     * Cancellation reason or additional context
     */
    private String reason;

    /**
     * Additional data
     */
    private String extraData;
}
