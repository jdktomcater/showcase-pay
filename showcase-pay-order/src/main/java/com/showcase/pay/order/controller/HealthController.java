package com.showcase.pay.order.controller;

import com.showcase.pay.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check controller for order service.
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Health", description = "Health check API")
public class HealthController {

    /**
     * Health check endpoint.
     *
     * @return health status
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check order service health")
    public Result<String> health() {
        return Result.success("Order service is running");
    }
}
