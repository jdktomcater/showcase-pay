package com.showcase.pay.common.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO representing the result of a payment operation.
 */
@Data
public class PaymentResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Payment ID
     */
    private Long paymentId;

    /**
     * Order ID
     */
    private Long orderId;

    /**
     * Payment method
     */
    private Integer paymentMethod;

    /**
     * Payment method description
     */
    private String paymentMethodName;

    /**
     * Payment amount
     */
    private BigDecimal amount;

    /**
     * Currency code
     */
    private String currency;

    /**
     * Payment status (0=Pending, 1=Processing, 2=Success, 3=Failed, etc.)
     */
    private Integer status;

    /**
     * Payment status description
     */
    private String statusDescription;

    /**
     * Third-party transaction ID (from payment gateway)
     */
    private String transactionId;

    /**
     * Payment completion time
     */
    private LocalDateTime payTime;

    /**
     * Error message (if payment failed)
     */
    private String errorMessage;

    /**
     * Redirect URL for third-party payment (if applicable)
     */
    private String redirectUrl;

    /**
     * Whether the payment was successful
     */
    public boolean isSuccess() {
        return status != null && status == 2;
    }
}
