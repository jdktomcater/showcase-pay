package com.showcase.pay.payment.mq;

import com.showcase.pay.common.constants.MqConstants;
import com.showcase.pay.common.enums.PaymentStatus;
import com.showcase.pay.payment.dto.PaymentDetailDTO;
import com.showcase.pay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumer for payment timeout events.
 * Handles delayed messages for payment timeout detection.
 * If the payment is still pending/processing after the timeout period,
 * it will automatically cancel the payment and notify other services.
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MqConstants.TOPIC_PAYMENT,
        consumerGroup = MqConstants.CG_PAYMENT,
        selectorExpression = MqConstants.TAG_PAYMENT_TIMEOUT
)
@RequiredArgsConstructor
public class PaymentTimeoutConsumer implements RocketMQListener<Map<String, String>> {

    private final PaymentService paymentService;

    @Override
    public void onMessage(Map<String, String> message) {
        String paymentNo = message.get("paymentNo");
        String orderNo = message.get("orderNo");

        if (paymentNo == null) {
            log.warn("Invalid payment timeout message: {}", message);
            return;
        }

        log.info("Received payment timeout event: paymentNo={}, orderNo={}", paymentNo, orderNo);

        try {
            // Query current payment status
            PaymentDetailDTO paymentDetail = paymentService.queryPaymentStatus(paymentNo);

            if (paymentDetail == null) {
                log.warn("Payment not found: paymentNo={}", paymentNo);
                return;
            }

            PaymentStatus currentStatus = paymentDetail.getStatus();

            // If still pending or processing, mark as timed out and cancel
            if (currentStatus == PaymentStatus.PENDING || currentStatus == PaymentStatus.PROCESSING) {
                log.info("Payment timed out: paymentNo={}, currentStatus={}. Cancelling...",
                        paymentNo, currentStatus);

                paymentService.cancelPayment(paymentNo);

                log.info("Successfully cancelled timed-out payment: paymentNo={}, orderNo={}",
                        paymentNo, orderNo);
            } else {
                log.info("Payment already in final state: paymentNo={}, status={}",
                        paymentNo, currentStatus);
            }
        } catch (Exception e) {
            log.error("Error processing payment timeout: paymentNo={}", paymentNo, e);
            throw e; // Re-throw for MQ retry
        }
    }
}
