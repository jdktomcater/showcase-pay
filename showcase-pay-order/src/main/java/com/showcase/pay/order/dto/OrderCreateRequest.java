package com.showcase.pay.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Order Creation Request DTO
 */
@Data
@Schema(description = "Order Creation Request")
public class OrderCreateRequest {

    @NotNull(message = "User ID is required")
    @Schema(description = "User ID")
    private Long userId;

    @NotNull(message = "Order amount is required")
    @DecimalMin(value = "0.01", message = "Order amount must be greater than 0")
    @Schema(description = "Order Amount")
    private BigDecimal amount;

    @Schema(description = "Currency", example = "CNY")
    private String currency = "CNY";

    @NotBlank(message = "Order subject is required")
    @Schema(description = "Order Subject")
    private String subject;

    @Schema(description = "Order Description")
    private String description;

    @Schema(description = "Extra Data (JSON)")
    private String extraData;
}
