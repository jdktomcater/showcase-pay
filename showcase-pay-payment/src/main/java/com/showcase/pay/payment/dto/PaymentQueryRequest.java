package com.showcase.pay.payment.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request DTO for querying payment records.
 */
@Data
public class PaymentQueryRequest implements Serializable {

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
     * Associated order ID
     */
    private Long orderId;

    /**
     * Associated order number
     */
    private String orderNo;

    /**
     * Third-party transaction ID
     */
    private String transactionId;

    /**
     * User ID
     */
    private Long userId;
}
