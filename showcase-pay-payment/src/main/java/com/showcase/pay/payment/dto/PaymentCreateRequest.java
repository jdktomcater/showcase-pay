package com.showcase.pay.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Payment Creation Request DTO
 */
@Data
@Schema(description = "Payment Creation Request")
public class PaymentCreateRequest {

    @NotBlank(message = "Order number is required")
    @Schema(description = "Order Number")
    private String orderNo;

    @NotNull(message = "User ID is required")
    @Schema(description = "User ID")
    private Long userId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    @Schema(description = "Payment Amount")
    private BigDecimal amount;

    @Schema(description = "Currency", example = "CNY")
    private String currency = "CNY";

    @NotBlank(message = "Payment method is required")
    @Schema(description = "Payment Method: ALIPAY, WECHAT, CARD")
    private String paymentMethod;

    @Schema(description = "Payment Subject")
    private String subject;

    @Schema(description = "Payment Description")
    private String description;

    @Schema(description = "Callback URL")
    private String callbackUrl;

    @Schema(description = "Extra Data (JSON)")
    private String extraData;
}
