package com.showcase.pay.payment.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.showcase.pay.common.enums.PaymentStatus;
import com.showcase.pay.common.exception.BusinessException;
import com.showcase.pay.payment.dto.*;
import com.showcase.pay.payment.entity.PaymentRecord;
import com.showcase.pay.payment.mapper.PaymentRecordMapper;
import com.showcase.pay.payment.service.PaymentService;
import com.showcase.pay.payment.mq.PaymentMessageProducer;
import com.showcase.pay.payment.strategy.PaymentStrategy;
import com.showcase.pay.payment.strategy.PaymentStrategyContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of PaymentService.
 * Handles payment processing, callback handling, refunds, and status queries.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRecordMapper paymentRecordMapper;
    private final PaymentStrategyContext paymentStrategyContext;
    private final PaymentMessageProducer paymentMessageProducer;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PAYMENT_CACHE_KEY = "payment:detail:";
    private static final long CACHE_EXPIRE_MINUTES = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("Creating payment: orderNo={}, method={}, amount={}",
                request.getOrderNo(), request.getPaymentMethod(), request.getAmount());

        // 1. Check if order already has a successful payment
        PaymentRecord existingPayment = paymentRecordMapper.selectByOrderId(request.getOrderId());
        if (existingPayment != null && existingPayment.getStatus() == PaymentStatus.SUCCESS) {
            throw new BusinessException("Order already has a successful payment");
        }

        // 2. Create payment record
        PaymentRecord record = buildPaymentRecord(request);
        paymentRecordMapper.insert(record);

        // 3. Execute payment through the appropriate strategy
        PaymentStrategy strategy = paymentStrategyContext.getStrategy(request.getPaymentMethod());
        PaymentResponse response = strategy.pay(request);

        // 4. Update payment record with gateway info
        record.setTransactionId(response.getTransactionId());
        record.setChannelCode(request.getPaymentMethod().name().toLowerCase());
        record.setExpireTime(response.getExpireTime());
        record.setStatus(PaymentStatus.PROCESSING);
        paymentRecordMapper.updateById(record);

        // 5. Populate response
        response.setPaymentId(record.getId());
        response.setPaymentNo(record.getPaymentNo());
        response.setStatus(PaymentStatus.PROCESSING);
        response.setCreateTime(record.getCreateTime());

        // 6. Send payment initiated message to MQ
        paymentMessageProducer.sendPaymentInitiatedMessage(record.getPaymentNo(), request.getOrderNo());

        log.info("Payment created successfully: paymentNo={}", record.getPaymentNo());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String processCallback(PaymentCallbackRequest callbackRequest) {
        log.info("Processing payment callback: paymentNo={}, tradeStatus={}",
                callbackRequest.getPaymentNo(), callbackRequest.getTradeStatus());

        // 1. Retrieve payment record
        PaymentRecord record = paymentRecordMapper.selectByPaymentNo(callbackRequest.getPaymentNo());
        if (record == null) {
            log.error("Payment record not found: paymentNo={}", callbackRequest.getPaymentNo());
            return paymentStrategyContext.getStrategy(callbackRequest.getPaymentMethod())
                    .buildFailureCallbackResponse();
        }

        // 2. Verify callback signature
        PaymentStrategy strategy = paymentStrategyContext.getStrategy(callbackRequest.getPaymentMethod());
        if (!strategy.verifyCallback(callbackRequest)) {
            log.error("Callback signature verification failed: paymentNo={}", callbackRequest.getPaymentNo());
            return strategy.buildFailureCallbackResponse();
        }

        // 3. Check if already processed (idempotency)
        if (record.getStatus() == PaymentStatus.SUCCESS) {
            log.warn("Payment already processed: paymentNo={}", callbackRequest.getPaymentNo());
            return strategy.buildSuccessCallbackResponse();
        }

        // 4. Update payment status based on callback result
        boolean isSuccess = "SUCCESS".equalsIgnoreCase(callbackRequest.getTradeStatus())
                || "TRADE_SUCCESS".equalsIgnoreCase(callbackRequest.getTradeStatus());

        if (isSuccess) {
            record.setStatus(PaymentStatus.SUCCESS);
            record.setTransactionId(callbackRequest.getTransactionId());
            record.setChannelCode(callbackRequest.getChannelCode());
            record.setPayTime(LocalDateTime.now());

            // Parse payTime from callback if available
            if (callbackRequest.getPayTime() != null) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    record.setPayTime(LocalDateTime.parse(callbackRequest.getPayTime(), formatter));
                } catch (Exception e) {
                    log.warn("Failed to parse callback payTime: {}", callbackRequest.getPayTime());
                }
            }
        } else {
            record.setStatus(PaymentStatus.FAILED);
            record.setErrorCode(callbackRequest.getErrorCode());
            record.setErrorMsg(callbackRequest.getErrorMsg());
        }

        record.setUpdateTime(LocalDateTime.now());
        paymentRecordMapper.updateById(record);

        // 5. Clear cache
        redisTemplate.delete(PAYMENT_CACHE_KEY + callbackRequest.getPaymentNo());

        // 6. Send payment result message to MQ
        if (isSuccess) {
            paymentMessageProducer.sendPaymentSuccessMessage(callbackRequest.getPaymentNo(), record.getOrderNo());
        } else {
            paymentMessageProducer.sendPaymentFailedMessage(callbackRequest.getPaymentNo(), record.getOrderNo());
        }

        log.info("Payment callback processed: paymentNo={}, status={}",
                callbackRequest.getPaymentNo(), isSuccess ? "SUCCESS" : "FAILED");

        return strategy.buildSuccessCallbackResponse();
    }

    @Override
    public PaymentDetailDTO queryPayment(PaymentQueryRequest queryRequest) {
        log.info("Querying payment: {}", queryRequest);

        PaymentRecord record = findPaymentRecord(queryRequest);
        if (record == null) {
            throw new BusinessException("Payment record not found");
        }

        return convertToDetailDTO(record);
    }

    @Override
    public Page<PaymentDetailDTO> queryPaymentList(Long userId, int pageNum, int pageSize) {
        log.info("Querying payment list: userId={}, page={}, size={}", userId, pageNum, pageSize);

        Page<PaymentRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getUserId, userId)
                .orderByDesc(PaymentRecord::getCreateTime);

        Page<PaymentRecord> resultPage = paymentRecordMapper.selectPage(page, wrapper);

        // Convert to DTOs
        Page<PaymentDetailDTO> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<PaymentDetailDTO> dtoList = resultPage.getRecords().stream()
                .map(this::convertToDetailDTO)
                .toList();
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String processRefund(RefundRequest refundRequest) {
        log.info("Processing refund: paymentNo={}, amount={}",
                refundRequest.getPaymentNo(), refundRequest.getRefundAmount());

        PaymentRecord record = paymentRecordMapper.selectByPaymentNo(refundRequest.getPaymentNo());
        if (record == null) {
            throw new BusinessException("Payment record not found");
        }

        if (record.getStatus() != PaymentStatus.SUCCESS) {
            throw new BusinessException("Only successful payments can be refunded");
        }

        // Execute refund through the appropriate strategy
        BigDecimal refundAmount = refundRequest.getRefundAmount() != null
                ? refundRequest.getRefundAmount()
                : record.getAmount();

        String refundResult = paymentStrategyContext.executeRefund(
                refundRequest.getPaymentNo(),
                refundAmount,
                refundRequest.getRefundReason(),
                record.getPaymentMethod()
        );

        // Update payment status
        record.setStatus(PaymentStatus.REFUNDED);
        record.setUpdateTime(LocalDateTime.now());
        paymentRecordMapper.updateById(record);

        // Clear cache
        redisTemplate.delete(PAYMENT_CACHE_KEY + refundRequest.getPaymentNo());

        log.info("Refund processed successfully: paymentNo={}", refundRequest.getPaymentNo());
        return refundResult;
    }

    @Override
    public PaymentDetailDTO queryPaymentStatus(String paymentNo) {
        log.info("Querying payment status from gateway: paymentNo={}", paymentNo);

        PaymentRecord record = paymentRecordMapper.selectByPaymentNo(paymentNo);
        if (record == null) {
            throw new BusinessException("Payment record not found");
        }

        // If already success, return cached result
        if (record.getStatus() == PaymentStatus.SUCCESS) {
            return convertToDetailDTO(record);
        }

        // Query gateway for latest status
        try {
            PaymentStrategy strategy = paymentStrategyContext.getStrategy(record.getPaymentMethod());
            String gatewayResult = strategy.queryPaymentStatus(paymentNo, record.getExtraParams());
            log.info("Gateway query result: {}", gatewayResult);

            // Parse gateway result and update status if changed
            // (simplified - in production, parse the JSON and extract status)
            if (gatewayResult.contains("SUCCESS") || gatewayResult.contains("TRADE_SUCCESS")) {
                record.setStatus(PaymentStatus.SUCCESS);
                record.setPayTime(LocalDateTime.now());
                paymentRecordMapper.updateById(record);
                redisTemplate.delete(PAYMENT_CACHE_KEY + paymentNo);
            }
        } catch (Exception e) {
            log.error("Failed to query payment status from gateway: paymentNo={}", paymentNo, e);
        }

        return convertToDetailDTO(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String cancelPayment(String paymentNo) {
        log.info("Cancelling payment: paymentNo={}", paymentNo);

        PaymentRecord record = paymentRecordMapper.selectByPaymentNo(paymentNo);
        if (record == null) {
            throw new BusinessException("Payment record not found");
        }

        if (record.getStatus() != PaymentStatus.PENDING
                && record.getStatus() != PaymentStatus.PROCESSING) {
            throw new BusinessException("Only pending or processing payments can be cancelled");
        }

        // Cancel through gateway
        String cancelResult = paymentStrategyContext.executeCancel(paymentNo, record.getPaymentMethod());

        // Update status
        record.setStatus(PaymentStatus.CANCELLED);
        record.setUpdateTime(LocalDateTime.now());
        paymentRecordMapper.updateById(record);

        // Clear cache
        redisTemplate.delete(PAYMENT_CACHE_KEY + paymentNo);

        log.info("Payment cancelled: paymentNo={}", paymentNo);
        return cancelResult;
    }

    /**
     * Find a payment record based on query criteria.
     */
    private PaymentRecord findPaymentRecord(PaymentQueryRequest request) {
        if (request.getPaymentId() != null) {
            return paymentRecordMapper.selectById(request.getPaymentId());
        }
        if (request.getPaymentNo() != null) {
            return paymentRecordMapper.selectByPaymentNo(request.getPaymentNo());
        }
        if (request.getOrderId() != null) {
            return paymentRecordMapper.selectByOrderId(request.getOrderId());
        }
        if (request.getTransactionId() != null) {
            return paymentRecordMapper.selectByTransactionId(request.getTransactionId());
        }
        return null;
    }

    /**
     * Build a new PaymentRecord from a PaymentRequest.
     */
    private PaymentRecord buildPaymentRecord(PaymentRequest request) {
        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo(generatePaymentNo());
        record.setOrderId(request.getOrderId());
        record.setOrderNo(request.getOrderNo());
        record.setUserId(request.getUserId());
        record.setAmount(request.getAmount());
        record.setCurrency(request.getCurrency() != null ? request.getCurrency() : "CNY");
        record.setPaymentMethod(request.getPaymentMethod());
        record.setStatus(PaymentStatus.PENDING);
        record.setSubject(request.getSubject());
        record.setBody(request.getBody());
        record.setClientIp(request.getClientIp());
        record.setNotifyUrl(request.getNotifyUrl());
        record.setExtraParams(request.getExtraParams());
        record.setRetryCount(0);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        return record;
    }

    /**
     * Generate a unique payment number.
     */
    private String generatePaymentNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String snowflake = IdUtil.getSnowflakeNextIdStr();
        return "PAY" + timestamp + snowflake.substring(snowflake.length() - 8);
    }

    /**
     * Convert PaymentRecord entity to PaymentDetailDTO.
     */
    private PaymentDetailDTO convertToDetailDTO(PaymentRecord record) {
        PaymentDetailDTO dto = new PaymentDetailDTO();
        dto.setId(record.getId());
        dto.setPaymentNo(record.getPaymentNo());
        dto.setOrderId(record.getOrderId());
        dto.setOrderNo(record.getOrderNo());
        dto.setUserId(record.getUserId());
        dto.setAmount(record.getAmount());
        dto.setCurrency(record.getCurrency());
        dto.setPaymentMethod(record.getPaymentMethod());
        dto.setStatus(record.getStatus());
        dto.setTransactionId(record.getTransactionId());
        dto.setChannelCode(record.getChannelCode());
        dto.setSubject(record.getSubject());
        dto.setBody(record.getBody());
        dto.setPayTime(record.getPayTime());
        dto.setExpireTime(record.getExpireTime());
        dto.setErrorCode(record.getErrorCode());
        dto.setErrorMsg(record.getErrorMsg());
        dto.setRetryCount(record.getRetryCount());
        dto.setCreateTime(record.getCreateTime());
        dto.setUpdateTime(record.getUpdateTime());
        return dto;
    }
}
