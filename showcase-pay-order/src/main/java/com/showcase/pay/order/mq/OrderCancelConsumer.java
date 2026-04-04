package com.showcase.pay.order.mq;

import com.showcase.pay.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * Order Cancellation Message Consumer
 * Listens for order cancellation events and performs cleanup:
 * - Verifies order exists and is in a cancellable state
 * - Releases inventory (placeholder for external service call)
 * - Notifies payment service to rollback pending payments
 */
@Slf4j
@Service
@RocketMQMessageListener(
        topic = "ORDER_CANCEL_TOPIC",
        consumerGroup = "order-cancel-consumer-group"
)
@RequiredArgsConstructor
public class OrderCancelConsumer implements RocketMQListener<OrderEventMessage> {

    private final OrderService orderService;

    @Override
    public void onMessage(OrderEventMessage message) {
        log.info("Received order cancellation event: orderNo={}, userId={}, reason={}",
                message.getOrderNo(), message.getUserId(), message.getReason());
        try {
            // Call order service to handle cancellation
            orderService.cancelOrder(message.getOrderNo());
            log.info("Successfully processed order cancellation: orderNo={}", message.getOrderNo());
        } catch (Exception e) {
            log.error("Failed to process order cancellation for orderNo={}: {}",
                    message.getOrderNo(), e.getMessage(), e);
            throw e;
        }
    }
}
