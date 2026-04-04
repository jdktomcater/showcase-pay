package com.showcase.pay.payment.dto;

import com.showcase.pay.common.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Request DTO for creating a payment.
 */
@Data
public class PaymentRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Associated order ID
     */
    @NotNull(message = "Order ID is required")
    private Long orderId;

    /**
     * Associated order number
     */
    @NotNull(message = "Order number is required")
    @Size(max = 64, message = "Order number length cannot exceed 64 characters")
    private String orderNo;

    /**
     * User ID
     */
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * Payment amount
     */
    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    private BigDecimal amount;

    /**
     * Payment currency (default: CNY)
     */
    @Size(max = 10, message = "Currency code length cannot exceed 10 characters")
    private String currency = "CNY";

    /**
     * Payment method
     */
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    /**
     * Payment subject/description
     */
    @Size(max = 256, message = "Subject length cannot exceed 256 characters")
    private String subject;

    /**
     * Payment body/details
     */
    @Size(max = 1024, message = "Body length cannot exceed 1024 characters")
    private String body;

    /**
     * Client IP address
     */
    private String clientIp;

    /**
     * Callback notification URL
     */
    @Size(max = 512, message = "Notify URL length cannot exceed 512 characters")
    private String notifyUrl;

    /**
     * Extra parameters (JSON string)
     */
    private String extraParams;
}
