package com.showcase.pay.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Result Code Enum
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "Success"),
    FAILURE(500, "Internal Server Error"),
    
    // Common business error codes
    PARAM_ERROR(400, "Invalid Parameter"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    
    // Payment related error codes
    PAYMENT_FAILED(1001, "Payment Failed"),
    PAYMENT_NOT_FOUND(1002, "Payment Record Not Found"),
    PAYMENT_STATUS_INVALID(1003, "Invalid Payment Status"),
    PAYMENT_AMOUNT_ERROR(1004, "Invalid Payment Amount"),
    PAYMENT_CHANNEL_ERROR(1005, "Payment Channel Error"),
    PAYMENT_DUPLICATE(1006, "Duplicate Payment"),
    
    // Order related error codes
    ORDER_FAILED(2001, "Order Creation Failed"),
    ORDER_NOT_FOUND(2002, "Order Not Found"),
    ORDER_STATUS_INVALID(2003, "Invalid Order Status"),
    ORDER_EXPIRED(2004, "Order Expired"),
    ORDER_AMOUNT_ERROR(2005, "Invalid Order Amount");

    private final Integer code;
    private final String message;
}
