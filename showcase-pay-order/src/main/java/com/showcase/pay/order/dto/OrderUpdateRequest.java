package com.showcase.pay.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Order Update Request DTO
 */
@Data
@Schema(description = "Order Update Request")
public class OrderUpdateRequest {

    @NotBlank(message = "Order subject is required")
    @Schema(description = "Order Subject")
    private String subject;

    @Schema(description = "Order Description")
    private String description;

    @Schema(description = "Extra Data (JSON)")
    private String extraData;
}
