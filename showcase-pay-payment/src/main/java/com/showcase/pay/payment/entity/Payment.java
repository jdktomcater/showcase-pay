package com.showcase.pay.payment.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.showcase.pay.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Payment Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment")
@Schema(description = "Payment Record")
public class Payment extends BaseEntity {

    @Schema(description = "Payment Number")
    @TableField("payment_no")
    private String paymentNo;

    @Schema(description = "Order Number")
    @TableField("order_no")
    private String orderNo;

    @Schema(description = "User ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "Payment Amount")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(description = "Currency")
    @TableField("currency")
    private String currency;

    @Schema(description = "Payment Method: ALIPAY, WECHAT, CARD, etc.")
    @TableField("payment_method")
    private String paymentMethod;

    @Schema(description = "Payment Status: PENDING, PROCESSING, SUCCESS, FAILED, CLOSED")
    @TableField("status")
    private String status;

    @Schema(description = "Third-party Payment Channel Transaction ID")
    @TableField("transaction_id")
    private String transactionId;

    @Schema(description = "Payment Subject")
    @TableField("subject")
    private String subject;

    @Schema(description = "Payment Description")
    @TableField("description")
    private String description;

    @Schema(description = "Payment Time")
    @TableField("pay_time")
    private java.time.LocalDateTime payTime;

    @Schema(description = "Expiration Time")
    @TableField("expire_time")
    private java.time.LocalDateTime expireTime;

    @Schema(description = "Callback URL")
    @TableField("callback_url")
    private String callbackUrl;

    @Schema(description = "Extra Data (JSON)")
    @TableField("extra_data")
    private String extraData;

    @Schema(description = "Error Message")
    @TableField("error_message")
    private String errorMessage;
}
