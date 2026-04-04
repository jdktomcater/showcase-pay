package com.showcase.pay.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.showcase.pay.common.enums.PaymentMethod;
import com.showcase.pay.common.enums.PaymentStatus;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment record entity mapped to the payment_record table.
 */
@Data
@TableName("payment_record")
public class PaymentRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Primary key ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Payment record number (business unique identifier)
     */
    @TableField("payment_no")
    private String paymentNo;

    /**
     * Associated order ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * Associated order number
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * User ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * Payment amount
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * Payment currency (default: CNY)
     */
    @TableField("currency")
    private String currency;

    /**
     * Payment method (1=Alipay, 2=WeChat Pay, 3=Bank Card, 4=Credit Card, 5=Balance, 6=Apple Pay)
     */
    @TableField("payment_method")
    private PaymentMethod paymentMethod;

    /**
     * Payment status (0=Pending, 1=Processing, 2=Success, 3=Failed, 4=Cancelled, 5=Refunded, 6=Timeout)
     */
    @TableField("status")
    private PaymentStatus status;

    /**
     * Third-party payment transaction number (from payment gateway)
     */
    @TableField("transaction_id")
    private String transactionId;

    /**
     * Third-party payment channel code
     */
    @TableField("channel_code")
    private String channelCode;

    /**
     * Payment subject/description
     */
    @TableField("subject")
    private String subject;

    /**
     * Payment body/details
     */
    @TableField("body")
    private String body;

    /**
     * Client IP address
     */
    @TableField("client_ip")
    private String clientIp;

    /**
     * Payment success time
     */
    @TableField("pay_time")
    private LocalDateTime payTime;

    /**
     * Payment expiration time
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * Error code from payment gateway
     */
    @TableField("error_code")
    private String errorCode;

    /**
     * Error message from payment gateway
     */
    @TableField("error_msg")
    private String errorMsg;

    /**
     * Extra parameters (JSON format)
     */
    @TableField("extra_params")
    private String extraParams;

    /**
     * Callback notification URL
     */
    @TableField("notify_url")
    private String notifyUrl;

    /**
     * Number of retries
     */
    @TableField("retry_count")
    private Integer retryCount;

    /**
     * Logical delete flag (0=not deleted, 1=deleted)
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_by")
    private String updateBy;
}
