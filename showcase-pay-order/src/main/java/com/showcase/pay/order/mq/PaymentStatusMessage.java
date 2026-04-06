package com.showcase.pay.order.mq;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Payment Status Message
 */
@Data
public class PaymentStatusMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String paymentNo;
    private String orderNo;
    private String transactionId;
    private String status;
    private BigDecimal amount;
    private Long timestamp;
}
