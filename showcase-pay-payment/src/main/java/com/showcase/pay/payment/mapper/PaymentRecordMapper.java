package com.showcase.pay.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.showcase.pay.common.enums.PaymentStatus;
import com.showcase.pay.payment.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MyBatis Plus mapper for PaymentRecord.
 */
@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    /**
     * Select a payment record by payment number.
     *
     * @param paymentNo the payment number
     * @return the payment record, or null if not found
     */
    PaymentRecord selectByPaymentNo(@Param("paymentNo") String paymentNo);

    /**
     * Select a payment record by transaction ID.
     *
     * @param transactionId the third-party transaction ID
     * @return the payment record, or null if not found
     */
    PaymentRecord selectByTransactionId(@Param("transactionId") String transactionId);

    /**
     * Select a payment record by order ID.
     *
     * @param orderId the order ID
     * @return the payment record, or null if not found
     */
    PaymentRecord selectByOrderId(@Param("orderId") Long orderId);

    /**
     * Update payment status with optimistic locking.
     *
     * @param id             the payment record ID
     * @param newStatus      the new status
     * @param expectedStatus the expected current status
     * @return number of rows affected
     */
    int updateStatusById(@Param("id") Long id,
                         @Param("newStatus") PaymentStatus newStatus,
                         @Param("expectedStatus") PaymentStatus expectedStatus);
}
