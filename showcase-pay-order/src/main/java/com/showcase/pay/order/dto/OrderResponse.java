package com.showcase.pay.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order Response DTO
 */
@Data
@Schema(description = "Order Response")
public class OrderResponse {

    @Schema(description = "Order ID")
    private Long id;

    @Schema(description = "Order Number")
    private String orderNo;

    @Schema(description = "User ID")
    private Long userId;

    @Schema(description = "Order Amount")
    private BigDecimal amount;

    @Schema(description = "Currency")
    private String currency;

    @Schema(description = "Order Status")
    private String status;

    @Schema(description = "Order Subject")
    private String subject;

    @Schema(description = "Order Description")
    private String description;

    @Schema(description = "Payment Time")
    private LocalDateTime payTime;

    @Schema(description = "Expiration Time")
    private LocalDateTime expireTime;

    @Schema(description = "Create Time")
    private LocalDateTime createTime;
}
