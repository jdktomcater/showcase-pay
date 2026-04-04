package com.showcase.pay.payment.mq;

import com.showcase.pay.common.constants.MqConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Producer for payment-related RocketMQ messages.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentMessageProducer {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * Send payment initiated message.
     *
     * @param paymentNo payment number
     * @param orderNo   order number
     */
    public void sendPaymentInitiatedMessage(String paymentNo, String orderNo) {
        String destination = MqConstants.TOPIC_PAYMENT + ":" + MqConstants.TAG_PAYMENT_SUCCESS;
        Map<String, String> message = new HashMap<>();
        message.put("paymentNo", paymentNo);
        message.put("orderNo", orderNo);
        message.put("eventType", "PAYMENT_INITIATED");

        log.info("Sending payment initiated message: destination={}, paymentNo={}", destination, paymentNo);
        rocketMQTemplate.send(destination, MessageBuilder.withPayload(message).build());
    }

    /**
     * Send payment success message.
     *
     * @param paymentNo payment number
     * @param orderNo   order number
     */
    public void sendPaymentSuccessMessage(String paymentNo, String orderNo) {
        String destination = MqConstants.TOPIC_PAYMENT + ":" + MqConstants.TAG_PAYMENT_SUCCESS;
        Map<String, String> message = new HashMap<>();
        message.put("paymentNo", paymentNo);
        message.put("orderNo", orderNo);
        message.put("eventType", "PAYMENT_SUCCESS");

        log.info("Sending payment success message: destination={}, paymentNo={}", destination, paymentNo);
        rocketMQTemplate.send(destination, MessageBuilder.withPayload(message).build());
    }

    /**
     * Send payment failed message.
     *
     * @param paymentNo payment number
     * @param orderNo   order number
     */
    public void sendPaymentFailedMessage(String paymentNo, String orderNo) {
        String destination = MqConstants.TOPIC_PAYMENT + ":" + MqConstants.TAG_PAYMENT_FAILED;
        Map<String, String> message = new HashMap<>();
        message.put("paymentNo", paymentNo);
        message.put("orderNo", orderNo);
        message.put("eventType", "PAYMENT_FAILED");

        log.info("Sending payment failed message: destination={}, paymentNo={}", destination, paymentNo);
        rocketMQTemplate.send(destination, MessageBuilder.withPayload(message).build());
    }

    /**
     * Send payment timeout message.
     *
     * @param paymentNo payment number
     * @param orderNo   order number
     */
    public void sendPaymentTimeoutMessage(String paymentNo, String orderNo) {
        String destination = MqConstants.TOPIC_PAYMENT + ":" + MqConstants.TAG_PAYMENT_TIMEOUT;
        Map<String, String> message = new HashMap<>();
        message.put("paymentNo", paymentNo);
        message.put("orderNo", orderNo);
        message.put("eventType", "PAYMENT_TIMEOUT");

        log.info("Sending payment timeout message: destination={}, paymentNo={}", destination, paymentNo);
        rocketMQTemplate.syncSendDelayTimeMills(destination, MessageBuilder.withPayload(message).build(), 30 * 60 * 1000L);
    }
}
