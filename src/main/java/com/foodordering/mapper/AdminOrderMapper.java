package com.foodordering.mapper;

import com.foodordering.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminOrderMapper {
    List<Order> findAll(@Param("status") String status);
    Order findByOrderNo(@Param("orderNo") String orderNo);
    Order findByOrderNoForUpdate(@Param("orderNo") String orderNo);
    int updateStatus(@Param("orderNo") String orderNo, @Param("status") String status);
}
