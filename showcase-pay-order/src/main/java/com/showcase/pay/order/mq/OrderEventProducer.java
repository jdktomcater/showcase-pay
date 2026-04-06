package com.showcase.pay.order.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Order Event Message Producer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * Topic for order creation events
     */
    private static final String ORDER_CREATE_TOPIC = "ORDER_CREATE_TOPIC";

    /**
     * Topic for order cancellation events
     */
    private static final String ORDER_CANCEL_TOPIC = "ORDER_CANCEL_TOPIC";

    /**
     * Topic for order status change events
     */
    private static final String ORDER_STATUS_CHANGE_TOPIC = "ORDER_STATUS_CHANGE_TOPIC";

    /**
     * Send order created event
     */
    public void sendOrderCreatedEvent(OrderEventMessage message) {
        sendMessage(ORDER_CREATE_TOPIC, message);
    }

    /**
     * Send order cancelled event
     */
    public void sendOrderCancelledEvent(OrderEventMessage message) {
        sendMessage(ORDER_CANCEL_TOPIC, message);
    }

    /**
     * Send order status changed event
     */
    public void sendOrderStatusChangedEvent(OrderEventMessage message) {
        sendMessage(ORDER_STATUS_CHANGE_TOPIC, message);
    }

    /**
     * Send message to specified topic
     */
    private void sendMessage(String topic, OrderEventMessage message) {
        try {
            Message<OrderEventMessage> mqMessage = MessageBuilder.withPayload(message).build();
            rocketMQTemplate.syncSend(topic, mqMessage);
            log.info("Sent order event to {}: orderNo={}, eventType={}", topic, message.getOrderNo(), message.getEventType());
        } catch (Exception e) {
            log.error("Failed to send order event to {}: orderNo={}", topic, message.getOrderNo(), e);
            throw e;
        }
    }
}
