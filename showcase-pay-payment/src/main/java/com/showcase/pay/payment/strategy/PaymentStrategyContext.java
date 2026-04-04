package com.showcase.pay.payment.strategy;

import com.showcase.pay.common.enums.PaymentMethod;
import com.showcase.pay.payment.dto.PaymentCallbackRequest;
import com.showcase.pay.payment.dto.PaymentRequest;
import com.showcase.pay.payment.dto.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Payment strategy context that manages and selects the appropriate payment strategy.
 * Acts as a registry for all available payment strategies.
 */
@Slf4j
@Component
public class PaymentStrategyContext {

    /**
     * Strategy registry: maps PaymentMethod to its corresponding strategy.
     */
    private final Map<PaymentMethod, PaymentStrategy> strategyMap = new ConcurrentHashMap<>();

    /**
     * Register a payment strategy.
     *
     * @param strategy the payment strategy to register
     */
    public void registerStrategy(PaymentStrategy strategy) {
        if (strategy != null && strategy.getPaymentMethod() != null) {
            strategyMap.put(strategy.getPaymentMethod(), strategy);
            log.info("Registered payment strategy: {}", strategy.getPaymentMethod().getDescription());
        }
    }

    /**
     * Get the payment strategy for the specified payment method.
     *
     * @param paymentMethod the payment method
     * @return the corresponding payment strategy
     * @throws IllegalArgumentException if no strategy is found for the payment method
     */
    public PaymentStrategy getStrategy(PaymentMethod paymentMethod) {
        PaymentStrategy strategy = strategyMap.get(paymentMethod);
        if (strategy == null) {
            throw new IllegalArgumentException("No payment strategy found for: " + paymentMethod);
        }
        return strategy;
    }

    /**
     * Check if a strategy exists for the given payment method.
     *
     * @param paymentMethod the payment method
     * @return true if a strategy exists, false otherwise
     */
    public boolean hasStrategy(PaymentMethod paymentMethod) {
        return strategyMap.containsKey(paymentMethod);
    }

    /**
     * Get all registered payment methods.
     *
     * @return set of registered payment methods
     */
    public java.util.Set<PaymentMethod> getSupportedMethods() {
        return strategyMap.keySet();
    }

    /**
     * Execute a payment using the appropriate strategy.
     *
     * @param request the payment request
     * @return payment response
     */
    public PaymentResponse executePayment(PaymentRequest request) {
        PaymentStrategy strategy = getStrategy(request.getPaymentMethod());
        return strategy.pay(request);
    }

    /**
     * Process a refund using the appropriate strategy.
     *
     * @param paymentNo    the payment number
     * @param refundAmount the refund amount
     * @param refundReason the refund reason
     * @param paymentMethod the payment method
     * @return refund result as JSON string
     */
    public String executeRefund(String paymentNo, BigDecimal refundAmount, String refundReason, PaymentMethod paymentMethod) {
        PaymentStrategy strategy = getStrategy(paymentMethod);
        return strategy.refund(paymentNo, refundAmount, refundReason);
    }

    /**
     * Cancel a payment using the appropriate strategy.
     *
     * @param paymentNo     the payment number
     * @param paymentMethod the payment method
     * @return cancel result as JSON string
     */
    public String executeCancel(String paymentNo, PaymentMethod paymentMethod) {
        PaymentStrategy strategy = getStrategy(paymentMethod);
        return strategy.cancel(paymentNo);
    }
}
