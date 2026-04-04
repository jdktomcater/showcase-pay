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
 * Bank card payment strategy implementation.
 * Provides mock integration with bank card payment gateway for demonstration.
 */
@Slf4j
@Component
public class BankCardStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.BANK_CARD;
    }

    @Override
    public PaymentResponse pay(PaymentRequest request) {
        log.info("Initiating Bank Card payment: orderNo={}, amount={}", request.getOrderNo(), request.getAmount());

        PaymentResponse response = new PaymentResponse();
        response.setPaymentMethod(PaymentMethod.BANK_CARD);
        response.setAmount(request.getAmount());
        response.setCurrency(request.getCurrency());
        response.setOrderId(request.getOrderId());
        response.setOrderNo(request.getOrderNo());

        // Generate mock transaction ID
        String transactionId = "BANK_" + UUID.randomUUID().toString().replace("-", "");
        response.setTransactionId(transactionId);

        // For bank card, typically need a payment form for redirect to banking page
        response.setPayForm("""
                <form action="https://bank-gateway.example.com/pay" method="post">
                    <input type="hidden" name="orderId" value="%s"/>
                    <input type="hidden" name="amount" value="%s"/>
                    <input type="hidden" name="transactionId" value="%s"/>
                </form>
                """.formatted(request.getOrderNo(), request.getAmount(), transactionId));

        // Set expiration time (15 minutes from now)
        response.setExpireTime(LocalDateTime.now().plusMinutes(15));

        log.info("Bank Card payment initiated: transactionId={}", transactionId);
        return response;
    }

    @Override
    public String queryPaymentStatus(String paymentNo, String extraParams) {
        log.info("Querying Bank Card payment status: paymentNo={}", paymentNo);

        return """
                {
                    "status": "SUCCESS",
                    "paymentNo": "%s",
                    "transactionId": "BANK_TXN_%s"
                }
                """.formatted(paymentNo, paymentNo);
    }

    @Override
    public String refund(String paymentNo, BigDecimal refundAmount, String refundReason) {
        log.info("Processing Bank Card refund: paymentNo={}, amount={}, reason={}",
                paymentNo, refundAmount, refundReason);

        String refundId = "BANK_REFUND_" + UUID.randomUUID().toString().replace("-", "");
        return """
                {
                    "status": "SUCCESS",
                    "refund_id": "%s",
                    "refund_amount": "%s"
                }
                """.formatted(refundId, refundAmount);
    }

    @Override
    public String cancel(String paymentNo) {
        log.info("Cancelling Bank Card payment: paymentNo={}", paymentNo);

        return """
                {
                    "status": "SUCCESS",
                    "paymentNo": "%s",
                    "action": "cancelled"
                }
                """.formatted(paymentNo);
    }

    @Override
    public boolean verifyCallback(PaymentCallbackRequest callbackRequest) {
        log.info("Verifying Bank Card callback: paymentNo={}", callbackRequest.getPaymentNo());

        // Mock signature verification
        return true;
    }

    @Override
    public String buildSuccessCallbackResponse() {
        return "{\"status\":\"success\"}";
    }

    @Override
    public String buildFailureCallbackResponse() {
        return "{\"status\":\"failure\"}";
    }
}
