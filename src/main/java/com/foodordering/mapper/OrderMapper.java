package com.foodordering.mapper;

import com.foodordering.entity.Order;
import com.foodordering.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderMapper {
    int insert(Order order);
    int insertItem(OrderItem item);
    List<Order> findByUserId(@Param("userId") Long userId);
    Order findByOrderNo(@Param("userId") Long userId, @Param("orderNo") String orderNo);
    Order findByOrderNoForUpdate(@Param("userId") Long userId, @Param("orderNo") String orderNo);
    List<OrderItem> findItemsByOrderId(@Param("orderId") Long orderId);
    int pay(@Param("userId") Long userId, @Param("orderNo") String orderNo);
    int cancel(@Param("userId") Long userId, @Param("orderNo") String orderNo, @Param("currentStatus") String currentStatus);
    int updateTotal(@Param("id") Long id, @Param("totalAmount") BigDecimal totalAmount);
    int countCompletedItem(@Param("userId") Long userId, @Param("orderId") Long orderId, @Param("foodId") Long foodId);
}
