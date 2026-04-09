package com.showcase.pay.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Authentication Global Filter
 * <p>
 * Intercepts all incoming requests to validate authentication tokens.
 * Requests without valid tokens are rejected with 401 Unauthorized.
 * Whitelist paths (e.g., health checks, swagger) are skipped.
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String AUTH_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Paths that do not require authentication
     */
    private static final List<String> WHITE_LIST = List.of(
            "/actuator/**",
            "/api/order/actuator/**",
            "/api/order/health",
            "/api/order/**",       // Added for demo access
            "/api/payment/actuator/**",
            "/api/payment/health",
            "/api/payment/**",     // Added for demo access
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/doc.html"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Skip authentication for whitelisted paths
        if (isWhitelisted(path)) {
            log.debug("Skipping auth for whitelisted path: {}", path);
            return chain.filter(exchange);
        }

        // Extract Authorization header
        String authHeader = request.getHeaders().getFirst(AUTH_HEADER);

        // Check if token is present
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Unauthorized access attempt to path: {}", path);
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
        }

        // Extract token
        String token = authHeader.substring(BEARER_PREFIX.length());

        // Validate token (basic validation - extend with JWT validation as needed)
        if (!isValidToken(token)) {
            log.warn("Invalid token for path: {}", path);
            return unauthorizedResponse(exchange, "Invalid or expired token");
        }

        // Add user info to request headers for downstream services
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Token", token)
                .header("X-Request-Path", path)
                .build();

        log.debug("Authentication successful for path: {}", path);
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    @Override
    public int getOrder() {
        // Execute before other filters
        return -100;
    }

    /**
     * Check if the path is in the whitelist
     */
    private boolean isWhitelisted(String path) {
        return WHITE_LIST.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * Validate the token
     * <p>
     * Basic validation - in production, integrate with JWT validation or
     * call an authentication service via OpenFeign.
     */
    private boolean isValidToken(String token) {
        // TODO: Implement proper JWT validation or call auth service
        return StringUtils.hasText(token) && token.length() > 10;
    }

    /**
     * Return 401 Unauthorized response
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        String body = "{\"code\":401,\"message\":\"" + message + "\",\"timestamp\":" + System.currentTimeMillis() + "}";
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}
