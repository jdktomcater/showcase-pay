package com.showcase.pay.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Standardized error codes for business exceptions.
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(200, "Success"),
    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource not found"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),

    // Business error codes (1xxx)
    ORDER_NOT_FOUND(1001, "Order not found"),
    ORDER_STATUS_INVALID(1002, "Invalid order status"),
    ORDER_CREATE_FAILED(1003, "Failed to create order"),
    ORDER_CANCEL_FAILED(1004, "Failed to cancel order"),

    // Payment error codes (2xxx)
    PAYMENT_NOT_FOUND(2001, "Payment not found"),
    PAYMENT_FAILED(2002, "Payment failed"),
    PAYMENT_TIMEOUT(2003, "Payment timeout"),
    PAYMENT_METHOD_UNSUPPORTED(2004, "Unsupported payment method"),
    PAYMENT_AMOUNT_INVALID(2005, "Invalid payment amount"),
    PAYMENT_DUPLICATE(2006, "Duplicate payment detected"),

    // User error codes (3xxx)
    USER_NOT_FOUND(3001, "User not found"),
    USER_BALANCE_INSUFFICIENT(3002, "Insufficient balance"),

    // Redis/cache error codes (4xxx)
    CACHE_ERROR(4001, "Cache operation failed"),
    CACHE_KEY_NOT_FOUND(4002, "Cache key not found"),

    // Parameter validation error codes (5xxx)
    PARAM_VALIDATION_FAILED(5001, "Parameter validation failed"),
    PARAM_MISSING(5002, "Required parameter missing"),

    // Crypto exchange error codes (6xxx)
    CRYPTO_INSUFFICIENT_BALANCE(6001, "Insufficient balance"),
    CRYPTO_ORDER_NOT_FOUND(6002, "Order not found"),
    CRYPTO_ORDER_STATUS_INVALID(6003, "Invalid order status"),
    CRYPTO_TRADING_PAIR_SUSPENDED(6004, "Trading pair suspended"),
    CRYPTO_ORDER_AMOUNT_TOO_SMALL(6005, "Order amount too small"),
    CRYPTO_ORDER_AMOUNT_TOO_LARGE(6006, "Order amount too large"),
    CRYPTO_PRICE_DEVIATION_TOO_HIGH(6007, "Price deviation too high"),
    CRYPTO_MATCHING_ENGINE_ERROR(6008, "Matching engine error"),
    CRYPTO_ASSET_NOT_SUPPORTED(6009, "Asset not supported"),
    CRYPTO_ORDER_TOO_MANY(6010, "Too many open orders"),
    CRYPTO_ORDER_CANCEL_FAILED(6011, "Failed to cancel order");

    private final Integer code;
    private final String message;
}
