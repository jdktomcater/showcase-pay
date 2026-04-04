package com.showcase.pay.common.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for creating a new order.
 */
@Data
public class CreateOrderDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * User ID who creates the order
     */
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * Product ID
     */
    @NotNull(message = "Product ID is required")
    private Long productId;

    /**
     * Order amount
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    /**
     * Currency code (e.g., CNY, USD)
     */
    @NotNull(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency code must be 3 characters")
    private String currency;

    /**
     * Order description
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /**
     * Business reference number (e.g., external order ID)
     */
    @Size(max = 64, message = "Business number must not exceed 64 characters")
    private String bizNo;
}
