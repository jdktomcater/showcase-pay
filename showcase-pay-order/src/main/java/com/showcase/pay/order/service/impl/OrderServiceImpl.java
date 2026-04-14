package com.showcase.pay.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.showcase.pay.common.exception.BusinessException;
import com.showcase.pay.common.exception.ErrorCode;
import com.showcase.pay.order.dto.OrderCreateRequest;
import com.showcase.pay.order.dto.OrderResponse;
import com.showcase.pay.order.entity.Order;
import com.showcase.pay.order.mapper.OrderMapper;
import com.showcase.pay.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Order Service Implementation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String ORDER_CACHE_PREFIX = "order:info:";
    private static final long CACHE_EXPIRE_HOURS = 2;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(OrderCreateRequest request) {
        // Generate order number
        String orderNo = "ORD" + IdUtil.getSnowflakeNextIdStr();
        Order order = new Order();
        BeanUtils.copyProperties(request, order);
        order.setOrderNo(orderNo);
        order.setStatus("CREATED");
        order.setExpireTime(LocalDateTime.now().plusMinutes(30));
        orderMapper.insert(order);
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        log.info("Order created: orderNo={}, userId={}", orderNo, request.getUserId());
        return convertToResponse(order);
    }

    @Override
    public OrderResponse getOrderByOrderNo(String orderNo) {
        // Try to get from cache
        String cacheKey = ORDER_CACHE_PREFIX + orderNo;
        OrderResponse cached = (OrderResponse) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        // Query from database
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderMapper.selectOne(wrapper);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        OrderResponse response = convertToResponse(order);
        // Cache the result
        redisTemplate.opsForValue().set(cacheKey, response, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        return response;
    }

    @Override
    public Page<OrderResponse> queryOrdersByUserId(Long userId, Integer pageNum, Integer pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId).orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);
        Page<OrderResponse> responsePage = new Page<>(pageNum, pageSize, orderPage.getTotal());
        responsePage.setRecords(orderPage.getRecords().stream().map(this::convertToResponse).toList());
        return responsePage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderMapper.selectOne(wrapper);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        if (!"CREATED".equals(order.getStatus()) && !"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID, "Only created or pending payment orders can be cancelled");
        }
        order.setStatus("CANCELLED");
        orderMapper.updateById(order);
        // Clear cache
        redisTemplate.delete(ORDER_CACHE_PREFIX + orderNo);
        log.info("Order cancelled: orderNo={}", orderNo);
        log.info("Order cancelled: orderNo={}", orderNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderPaymentStatus(String orderNo, String paymentStatus) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderMapper.selectOne(wrapper);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        if ("SUCCESS".equals(paymentStatus)) {
            order.setStatus("PAID");
            order.setPayTime(LocalDateTime.now());
        } else if ("FAILED".equals(paymentStatus)) {
            order.setStatus("CREATED");
        }
        orderMapper.updateById(order);
        // Clear cache
        redisTemplate.delete(ORDER_CACHE_PREFIX + orderNo);
        log.info("Order payment status updated: orderNo={}, paymentStatus={}", orderNo, paymentStatus);
        log.info("Order payment status updated: orderNo={}, paymentStatus={}", orderNo, paymentStatus);
    }

    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);
        return response;
    }
}
