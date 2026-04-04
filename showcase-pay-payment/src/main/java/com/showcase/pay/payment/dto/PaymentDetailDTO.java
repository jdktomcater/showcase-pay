package com.showcase.pay.payment.dto;

import com.showcase.pay.common.enums.PaymentMethod;
import com.showcase.pay.common.enums.PaymentStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for payment record details.
 */
@Data
public class PaymentDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Payment record ID
     */
    private Long id;

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
     * User ID
     */
    private Long userId;

    /**
     * Payment amount
     */
    private BigDecimal amount;

    /**
     * Payment currency
     */
    private String currency;

    /**
     * Payment method
     */
    private PaymentMethod paymentMethod;

    /**
     * Payment status
     */
    private PaymentStatus status;

    /**
     * Third-party transaction ID
     */
    private String transactionId;

    /**
     * Payment channel code
     */
    private String channelCode;

    /**
     * Payment subject
     */
    private String subject;

    /**
     * Payment body
     */
    private String body;

    /**
     * Payment success time
     */
    private LocalDateTime payTime;

    /**
     * Payment expiration time
     */
    private LocalDateTime expireTime;

    /**
     * Error code
     */
    private String errorCode;

    /**
     * Error message
     */
    private String errorMsg;

    /**
     * Number of retries
     */
    private Integer retryCount;

    /**
     * Creation time
     */
    private LocalDateTime createTime;

    /**
     * Last update time
     */
    private LocalDateTime updateTime;
}
