package com.showcase.pay.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Order Query Request DTO
 */
@Data
@Schema(description = "Order Query Request")
public class OrderQueryRequest {

    @Schema(description = "User ID")
    private Long userId;

    @Schema(description = "Order Status")
    private String status;

    @Schema(description = "Order Number")
    private String orderNo;

    @Schema(description = "Page Number", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "Page Size", example = "10")
    private Integer pageSize = 10;
}
