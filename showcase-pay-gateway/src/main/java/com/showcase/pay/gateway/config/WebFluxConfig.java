package com.showcase.pay.gateway.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;
import reactor.util.context.Context;

/**
 * WebFlux configuration to enable MDC propagation across reactive streams.
 * Ensures trace ID is preserved across thread switches in reactive pipelines.
 */
@Slf4j
@Configuration
public class WebFluxConfig {

    @PostConstruct
    public void init() {
        // Enable MDC propagation for Reactor
        Hooks.enableAutomaticContextPropagation();
        log.info("WebFlux MDC propagation enabled");
    }
}
