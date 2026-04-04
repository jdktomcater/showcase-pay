package com.showcase.pay.order.mq;

import com.showcase.pay.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * Payment Success Message Consumer
 * Listens for payment success events and updates order status to PAID.
 */
@Slf4j
@Service
@RocketMQMessageListener(
        topic = "PAYMENT_STATUS_TOPIC",
        consumerGroup = "order-payment-consumer-group",
        selectorExpression = "SUCCESS"
)
@RequiredArgsConstructor
public class PaymentSuccessConsumer implements RocketMQListener<PaymentStatusMessage> {

    private final OrderService orderService;

    @Override
    public void onMessage(PaymentStatusMessage message) {
        log.info("Received payment success message: orderNo={}, paymentNo={}, amount={}",
                message.getOrderNo(), message.getPaymentNo(), message.getAmount());
        try {
            orderService.updateOrderPaymentStatus(message.getOrderNo(), message.getStatus());
            log.info("Successfully updated order status to PAID for orderNo={}", message.getOrderNo());
        } catch (Exception e) {
            log.error("Failed to update order payment status for orderNo={}: {}",
                    message.getOrderNo(), e.getMessage(), e);
            throw e;
        }
    }
}
