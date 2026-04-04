package com.showcase.pay.payment.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request DTO for refund operations.
 */
@Data
public class RefundRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Payment record ID
     */
    private Long paymentId;

    /**
     * Payment record number
     */
    private String paymentNo;

    /**
     * Refund amount (if partial refund)
     * If null or equals original amount, performs full refund
     */
    private java.math.BigDecimal refundAmount;

    /**
     * Refund reason
     */
    private String refundReason;

    /**
     * Operator ID
     */
    private Long operatorId;
}
