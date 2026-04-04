package com.showcase.pay.order.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.showcase.pay.common.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * Order Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("orders")
@Schema(description = "Order")
public class Order extends BaseEntity {

    @Schema(description = "Order Number")
    @TableField("order_no")
    private String orderNo;

    @Schema(description = "User ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "Order Amount")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(description = "Currency")
    @TableField("currency")
    private String currency;

    @Schema(description = "Order Status: CREATED, PENDING_PAYMENT, PAID, SHIPPED, COMPLETED, CANCELLED, REFUNDED")
    @TableField("status")
    private String status;

    @Schema(description = "Order Subject")
    @TableField("subject")
    private String subject;

    @Schema(description = "Order Description")
    @TableField("description")
    private String description;

    @Schema(description = "Payment Time")
    @TableField("pay_time")
    private java.time.LocalDateTime payTime;

    @Schema(description = "Expiration Time")
    @TableField("expire_time")
    private java.time.LocalDateTime expireTime;

    @Schema(description = "Extra Data (JSON)")
    @TableField("extra_data")
    private String extraData;
}
