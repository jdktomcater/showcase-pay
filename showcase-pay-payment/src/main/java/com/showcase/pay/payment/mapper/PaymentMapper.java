package com.showcase.pay.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.showcase.pay.payment.entity.Payment;
import org.apache.ibatis.annotations.Mapper;

/**
 * Payment Mapper
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
