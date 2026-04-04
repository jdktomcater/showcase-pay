package com.showcase.pay.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Payment status enum.
 */
@Getter
@AllArgsConstructor
public enum PaymentStatus {

    /**
     * Payment pending (waiting for user to pay)
     */
    PENDING(0, "Pending"),

    /**
     * Payment processing (payment gateway processing)
     */
    PROCESSING(1, "Processing"),

    /**
     * Payment successful
     */
    SUCCESS(2, "Success"),

    /**
     * Payment failed
     */
    FAILED(3, "Failed"),

    /**
     * Payment cancelled
     */
    CANCELLED(4, "Cancelled"),

    /**
     * Payment refunded
     */
    REFUNDED(5, "Refunded"),

    /**
     * Payment timeout
     */
    TIMEOUT(6, "Timeout");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String description;

    /**
     * Get PaymentStatus by code.
     *
     * @param code the status code
     * @return the corresponding PaymentStatus, or null if not found
     */
    public static PaymentStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
