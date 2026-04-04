package com.showcase.pay.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Request Log Global Filter
 * <p>
 * Logs all incoming requests with timing information, request details,
 * and response status. Also generates a unique trace ID for each request
 * to support distributed tracing.
 */
@Slf4j
@Component
public class RequestLogFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String REQUEST_START_TIME = "requestStartTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Instant startTime = Instant.now();

        // Generate or extract trace ID
        String rawTraceId = request.getHeaders().getFirst(TRACE_ID_HEADER);
        final String traceId = (rawTraceId == null || rawTraceId.isBlank())
                ? UUID.randomUUID().toString().replace("-", "")
                : rawTraceId;

        // Add trace ID to MDC for logging (SkyWalking uses 'tid' key)
        // Also store in Reactor Context for reactive thread propagation
        MDC.put("tid", traceId);
        MDC.put("traceId", traceId);

        String requestPath = request.getURI().getPath();
        HttpMethod method = request.getMethod();
        String clientIp = getClientIp(request);
        String queryString = request.getURI().getQuery();

        // Log request
        log.info(">>> [{}] {} {} | Client: {} | Query: {}",
                traceId, method, requestPath, clientIp,
                queryString != null ? queryString : "-");

        // Add trace ID to response headers
        exchange.getResponse().getHeaders().add(TRACE_ID_HEADER, traceId);

        // Store start time in exchange attributes
        exchange.getAttributes().put(REQUEST_START_TIME, startTime);

        // Capture effectively-final copies for the lambda
        final String logTraceId = traceId;
        final HttpMethod logMethod = method;
        final String logPath = requestPath;

        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put("traceId", traceId).put("tid", traceId))
                .doOnSuccess(v -> {
                    Instant endTime = Instant.now();
                    Duration duration = Duration.between(startTime, endTime);
                    Integer statusCode = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 0;
                    log.info("<<< [{}] {} {} | Status: {} | Duration: {}ms",
                            logTraceId, logMethod, logPath, statusCode, duration.toMillis());
                })
                .doOnError(e -> {
                    log.error("<<< [{}] {} {} | Error: {}", logTraceId, logMethod, logPath, e.getMessage());
                })
                .doFinally(signal -> {
                    MDC.remove("tid");
                    MDC.remove("traceId");
                });
    }

    @Override
    public int getOrder() {
        // Execute before auth filter
        return -200;
    }

    /**
     * Extract client IP address, considering X-Forwarded-For and other proxy headers
     */
    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // X-Forwarded-For can contain multiple IPs; take the first one
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp;
        }

        InetSocketAddress remoteAddress = request.getRemoteAddress();
        return remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";
    }
}
