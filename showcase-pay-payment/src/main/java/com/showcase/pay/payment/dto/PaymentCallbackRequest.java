package com.showcase.pay.payment.dto;

import com.showcase.pay.common.enums.PaymentMethod;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request DTO for callback notifications from payment gateways.
 */
@Data
public class PaymentCallbackRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Payment record number
     */
    private String paymentNo;

    /**
     * Third-party transaction ID
     */
    private String transactionId;

    /**
     * Payment channel code
     */
    private String channelCode;

    /**
     * Payment result status (success/failed)
     */
    private String tradeStatus;

    /**
     * Payment amount (from gateway, for verification)
     */
    private String amount;

    /**
     * Payment method
     */
    private PaymentMethod paymentMethod;

    /**
     * Payment success time
     */
    private String payTime;

    /**
     * Error code (if failed)
     */
    private String errorCode;

    /**
     * Error message (if failed)
     */
    private String errorMsg;

    /**
     * Raw callback data from gateway (for signature verification)
     */
    private String rawData;
}
