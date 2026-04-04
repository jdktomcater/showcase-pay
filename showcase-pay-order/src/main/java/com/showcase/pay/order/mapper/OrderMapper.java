package com.showcase.pay.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.showcase.pay.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * Select order by order number
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * Select orders by user ID
     */
    List<Order> selectByUserId(@Param("userId") Long userId);

    /**
     * Select expired unpaid orders
     */
    List<Order> selectExpiredUnpaidOrders(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Batch update order status
     */
    int batchUpdateStatus(@Param("orderIds") List<Long> orderIds, @Param("status") String status);
}
