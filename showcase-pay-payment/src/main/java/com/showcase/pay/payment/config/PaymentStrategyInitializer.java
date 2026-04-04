package com.showcase.pay.payment.config;

import com.showcase.pay.common.enums.PaymentMethod;
import com.showcase.pay.payment.strategy.PaymentStrategyContext;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Payment strategy initializer.
 * Automatically discovers and registers all PaymentStrategy beans.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStrategyInitializer {

    private final ApplicationContext applicationContext;
    private final PaymentStrategyContext paymentStrategyContext;

    @PostConstruct
    public void init() {
        log.info("Initializing payment strategies...");

        // Discover all PaymentStrategy beans and register them
        List<com.showcase.pay.payment.strategy.PaymentStrategy> strategies =
                applicationContext.getBeansOfType(com.showcase.pay.payment.strategy.PaymentStrategy.class)
                        .values()
                        .stream()
                        .toList();

        for (com.showcase.pay.payment.strategy.PaymentStrategy strategy : strategies) {
            paymentStrategyContext.registerStrategy(strategy);
            log.info("Registered strategy: {} -> {}",
                    strategy.getPaymentMethod().getDescription(),
                    strategy.getClass().getSimpleName());
        }

        log.info("Payment strategies initialized: {}", paymentStrategyContext.getSupportedMethods());
    }
}
