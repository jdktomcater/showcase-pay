package com.showcase.pay.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.showcase.pay.common.result.Result;
import com.showcase.pay.order.dto.OrderCreateRequest;
import com.showcase.pay.order.dto.OrderResponse;
import com.showcase.pay.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Order Controller
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order API", description = "Order management APIs")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Create Order")
    public Result<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return Result.success(response);
    }

    @GetMapping("/{orderNo}")
    @Operation(summary = "Get Order Details")
    public Result<OrderResponse> getOrder(
            @Parameter(description = "Order Number") @PathVariable String orderNo) {
        OrderResponse response = orderService.getOrderByOrderNo(orderNo);
        return Result.success(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Query User Orders")
    public Result<Page<OrderResponse>> queryUserOrders(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Page Number") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "Page Size") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<OrderResponse> page = orderService.queryOrdersByUserId(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @PostMapping("/{orderNo}/cancel")
    @Operation(summary = "Cancel Order")
    public Result<Void> cancelOrder(
            @Parameter(description = "Order Number") @PathVariable String orderNo) {
        orderService.cancelOrder(orderNo);
        return Result.success();
    }
}
