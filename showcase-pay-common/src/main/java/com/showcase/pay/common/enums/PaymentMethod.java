package com.showcase.pay.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Payment method enum.
 */
@Getter
@AllArgsConstructor
public enum PaymentMethod {

    /**
     * Alipay
     */
    ALIPAY(1, "Alipay"),

    /**
     * WeChat Pay
     */
    WECHAT_PAY(2, "WeChat Pay"),

    /**
     * Bank card / Debit card
     */
    BANK_CARD(3, "Bank Card"),

    /**
     * Credit card
     */
    CREDIT_CARD(4, "Credit Card"),

    /**
     * Balance (wallet balance)
     */
    BALANCE(5, "Balance"),

    /**
     * Apple Pay
     */
    APPLE_PAY(6, "Apple Pay");

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String description;

    /**
     * Get PaymentMethod by code.
     *
     * @param code the method code
     * @return the corresponding PaymentMethod, or null if not found
     */
    public static PaymentMethod getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PaymentMethod method : values()) {
            if (method.getCode().equals(code)) {
                return method;
            }
        }
        return null;
    }
}
