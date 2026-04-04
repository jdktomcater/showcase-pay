package com.showcase.pay.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Order status enum.
 */
@Getter
@AllArgsConstructor
public enum OrderStatus {

    /**
     * Order created, pending payment
     */
    CREATED(0, "Created"),

    /**
     * Payment in progress
     */
    PAYING(1, "Paying"),

    /**
     * Payment successful, order completed
     */
    PAID(2, "Paid"),

    /**
     * Order cancelled
     */
    CANCELLED(3, "Cancelled"),

    /**
     * Order closed (e.g., refunded)
     */
    CLOSED(4, "Closed");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String description;

    /**
     * Get OrderStatus by code.
     *
     * @param code the status code
     * @return the corresponding OrderStatus, or null if not found
     */
    public static OrderStatus getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (OrderStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
