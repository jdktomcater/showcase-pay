package com.showcase.pay.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.showcase.pay.order.dto.OrderCreateRequest;
import com.showcase.pay.order.dto.OrderResponse;

/**
 * Order Service Interface
 */
public interface OrderService {

    /**
     * Create order
     */
    OrderResponse createOrder(OrderCreateRequest request);

    /**
     * Get order by order number
     */
    OrderResponse getOrderByOrderNo(String orderNo);

    /**
     * Query orders by user ID
     */
    Page<OrderResponse> queryOrdersByUserId(Long userId, Integer pageNum, Integer pageSize);

    /**
     * Cancel order
     */
    void cancelOrder(String orderNo);

    /**
     * Update order payment status
     */
    void updateOrderPaymentStatus(String orderNo, String paymentStatus);
}
