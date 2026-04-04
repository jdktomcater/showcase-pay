package com.showcase.pay.payment.strategy;

import com.showcase.pay.common.enums.PaymentMethod;
import com.showcase.pay.payment.dto.PaymentCallbackRequest;
import com.showcase.pay.payment.dto.PaymentRequest;
import com.showcase.pay.payment.dto.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Alipay payment strategy implementation.
 * Provides mock integration with Alipay gateway for demonstration.
 */
@Slf4j
@Component
public class AlipayStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.ALIPAY;
    }

    @Override
    public PaymentResponse pay(PaymentRequest request) {
        log.info("Initiating Alipay payment: orderNo={}, amount={}", request.getOrderNo(), request.getAmount());

        PaymentResponse response = new PaymentResponse();
        response.setPaymentMethod(PaymentMethod.ALIPAY);
        response.setAmount(request.getAmount());
        response.setCurrency(request.getCurrency());
        response.setOrderId(request.getOrderId());
        response.setOrderNo(request.getOrderNo());

        // Generate mock Alipay transaction ID
        String transactionId = "ALI_" + UUID.randomUUID().toString().replace("-", "");
        response.setTransactionId(transactionId);

        // For web payment, generate redirect URL
        response.setRedirectUrl("https://openapi.alipay.com/gateway.do?transactionId=" + transactionId);

        // Set expiration time (30 minutes from now)
        response.setExpireTime(LocalDateTime.now().plusMinutes(30));

        log.info("Alipay payment initiated: transactionId={}", transactionId);
        return response;
    }

    @Override
    public String queryPaymentStatus(String paymentNo, String extraParams) {
        log.info("Querying Alipay payment status: paymentNo={}", paymentNo);

        // Mock response: simulate gateway query
        return """
                {
                    "code": "10000",
                    "msg": "Success",
                    "trade_status": "TRADE_SUCCESS",
                    "out_trade_no": "%s",
                    "trade_no": "ALI_TRADE_%s"
                }
                """.formatted(paymentNo, paymentNo);
    }

    @Override
    public String refund(String paymentNo, BigDecimal refundAmount, String refundReason) {
        log.info("Processing Alipay refund: paymentNo={}, amount={}, reason={}",
                paymentNo, refundAmount, refundReason);

        // Mock refund response
        String refundId = "REFUND_" + UUID.randomUUID().toString().replace("-", "");
        return """
                {
                    "code": "10000",
                    "msg": "Success",
                    "refund_id": "%s",
                    "refund_amount": "%s",
                    "refund_reason": "%s"
                }
                """.formatted(refundId, refundAmount, refundReason);
    }

    @Override
    public String cancel(String paymentNo) {
        log.info("Cancelling Alipay payment: paymentNo={}", paymentNo);

        // Mock cancel response
        return """
                {
                    "code": "10000",
                    "msg": "Success",
                    "out_trade_no": "%s",
                    "action": "closed"
                }
                """.formatted(paymentNo);
    }

    @Override
    public boolean verifyCallback(PaymentCallbackRequest callbackRequest) {
        log.info("Verifying Alipay callback: paymentNo={}", callbackRequest.getPaymentNo());

        // Mock signature verification - always return true for demo
        // In production, this would verify the RSA signature from Alipay
        return true;
    }

    @Override
    public String buildSuccessCallbackResponse() {
        return "success";
    }

    @Override
    public String buildFailureCallbackResponse() {
        return "failure";
    }
}
