package com.showcase.pay.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Gateway Application
 * <p>
 * Excludes Spring Security auto-configuration since authentication
 * is handled by the custom AuthGlobalFilter.
 */
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        ReactiveSecurityAutoConfiguration.class
})
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
