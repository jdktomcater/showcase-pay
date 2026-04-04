package com.showcase.pay.common.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for initiating a payment.
 */
@Data
public class PaymentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Order ID to pay for
     */
    @NotNull(message = "Order ID is required")
    private Long orderId;

    /**
     * User ID who initiates the payment
     */
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * Payment method (1=Alipay, 2=WeChat Pay, 3=Bank Card, etc.)
     */
    @NotNull(message = "Payment method is required")
    private Integer paymentMethod;

    /**
     * Payment amount
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    /**
     * Currency code (e.g., CNY, USD)
     */
    private String currency;

    /**
     * Client IP address
     */
    private String clientIp;

    /**
     * Additional payment metadata (JSON string)
     */
    private String extraInfo;
}
