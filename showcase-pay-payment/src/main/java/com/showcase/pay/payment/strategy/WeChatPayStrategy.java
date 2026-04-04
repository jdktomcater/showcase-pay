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
 * WeChat Pay payment strategy implementation.
 * Provides mock integration with WeChat Pay gateway for demonstration.
 */
@Slf4j
@Component
public class WeChatPayStrategy implements PaymentStrategy {

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.WECHAT_PAY;
    }

    @Override
    public PaymentResponse pay(PaymentRequest request) {
        log.info("Initiating WeChat Pay payment: orderNo={}, amount={}", request.getOrderNo(), request.getAmount());

        PaymentResponse response = new PaymentResponse();
        response.setPaymentMethod(PaymentMethod.WECHAT_PAY);
        response.setAmount(request.getAmount());
        response.setCurrency(request.getCurrency());
        response.setOrderId(request.getOrderId());
        response.setOrderNo(request.getOrderNo());

        // Generate mock WeChat Pay prepay ID
        String prepayId = "WX_PREPAY_" + UUID.randomUUID().toString().replace("-", "");
        response.setTransactionId(prepayId);

        // For native payment, generate QR code content
        String qrCodeContent = "weixin://wxpay/bizpayurl?pr=" + prepayId;
        response.setQrCode(qrCodeContent);

        // Set expiration time (2 hours from now)
        response.setExpireTime(LocalDateTime.now().plusHours(2));

        log.info("WeChat Pay payment initiated: prepayId={}", prepayId);
        return response;
    }

    @Override
    public String queryPaymentStatus(String paymentNo, String extraParams) {
        log.info("Querying WeChat Pay payment status: paymentNo={}", paymentNo);

        // Mock response
        return """
                {
                    "return_code": "SUCCESS",
                    "result_code": "SUCCESS",
                    "trade_state": "SUCCESS",
                    "out_trade_no": "%s",
                    "transaction_id": "WX_TXN_%s"
                }
                """.formatted(paymentNo, paymentNo);
    }

    @Override
    public String refund(String paymentNo, BigDecimal refundAmount, String refundReason) {
        log.info("Processing WeChat Pay refund: paymentNo={}, amount={}, reason={}",
                paymentNo, refundAmount, refundReason);

        String refundId = "WX_REFUND_" + UUID.randomUUID().toString().replace("-", "");
        return """
                {
                    "return_code": "SUCCESS",
                    "result_code": "SUCCESS",
                    "refund_id": "%s",
                    "refund_fee": "%s"
                }
                """.formatted(refundId, refundAmount);
    }

    @Override
    public String cancel(String paymentNo) {
        log.info("Cancelling WeChat Pay payment: paymentNo={}", paymentNo);

        return """
                {
                    "return_code": "SUCCESS",
                    "result_code": "SUCCESS",
                    "out_trade_no": "%s",
                    "trade_state": "CLOSED"
                }
                """.formatted(paymentNo);
    }

    @Override
    public boolean verifyCallback(PaymentCallbackRequest callbackRequest) {
        log.info("Verifying WeChat Pay callback: paymentNo={}", callbackRequest.getPaymentNo());

        // Mock signature verification - always return true for demo
        // In production, this would verify the HMAC-SHA256 or MD5 signature from WeChat Pay
        return true;
    }

    @Override
    public String buildSuccessCallbackResponse() {
        return """
                <xml>
                    <return_code><![CDATA[SUCCESS]]></return_code>
                    <return_msg><![CDATA[OK]]></return_msg>
                </xml>
                """;
    }

    @Override
    public String buildFailureCallbackResponse() {
        return """
                <xml>
                    <return_code><![CDATA[FAIL]]></return_code>
                    <return_msg><![CDATA[Signature verification failed]]></return_msg>
                </xml>
                """;
    }
}
