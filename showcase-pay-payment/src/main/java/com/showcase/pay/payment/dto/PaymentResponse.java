package com.showcase.pay.payment.dto;

import com.showcase.pay.common.enums.PaymentMethod;
import com.showcase.pay.common.enums.PaymentStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for payment creation result.
 */
@Data
public class PaymentResponse implements Serializable {

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
     * Payment gateway redirect URL (for web payments)
     */
    private String redirectUrl;

    /**
     * Payment form data (for app/H5 payments)
     */
    private String payForm;

    /**
     * QR code content (for QR code payments)
     */
    private String qrCode;

    /**
     * Third-party transaction ID
     */
    private String transactionId;

    /**
     * Payment expiration time
     */
    private LocalDateTime expireTime;

    /**
     * Creation time
     */
    private LocalDateTime createTime;
}
