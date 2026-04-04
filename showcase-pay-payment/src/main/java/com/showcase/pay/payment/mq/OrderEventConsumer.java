package com.showcase.pay.payment.mq;

import com.showcase.pay.common.constants.MqConstants;
import com.showcase.pay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Consumer for order events from the Order service.
 * Listens for order creation/cancellation events.
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MqConstants.TOPIC_ORDER,
        consumerGroup = MqConstants.CG_PAYMENT,
        selectorExpression = MqConstants.TAG_ORDER_CREATED + " || " + MqConstants.TAG_ORDER_CANCELLED
)
@RequiredArgsConstructor
public class OrderEventConsumer implements RocketMQListener<Map<String, String>> {

    private final PaymentService paymentService;

    @Override
    public void onMessage(Map<String, String> message) {
        log.info("Received order event: {}", message);

        String eventType = message.get("eventType");
        String orderNo = message.get("orderNo");

        if (eventType == null || orderNo == null) {
            log.warn("Invalid order event message: {}", message);
            return;
        }

        try {
            switch (eventType) {
                case "ORDER_CREATED" -> handleOrderCreated(message);
                case "ORDER_CANCELLED" -> handleOrderCancelled(message);
                default -> log.warn("Unknown order event type: {}", eventType);
            }
        } catch (Exception e) {
            log.error("Error processing order event: {}", message, e);
            throw e; // Re-throw for retry
        }
    }

    /**
     * Handle order created event - may need to create a pending payment record.
     */
    private void handleOrderCreated(Map<String, String> message) {
        String orderNo = message.get("orderNo");
        String orderId = message.get("orderId");
        log.info("Order created event received: orderNo={}, orderId={}", orderNo, orderId);
        // Logic to prepare payment records if needed
    }

    /**
     * Handle order cancelled event - cancel any pending payments.
     */
    private void handleOrderCancelled(Map<String, String> message) {
        String orderNo = message.get("orderNo");
        log.info("Order cancelled event received: orderNo={}", orderNo);
        // Logic to cancel associated pending payments
    }
}
