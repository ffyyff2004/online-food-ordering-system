package com.foodordering.mapper;

import com.foodordering.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
    List<CartItem> findByUserId(@Param("userId") Long userId);
    CartItem findByUserAndFood(@Param("userId") Long userId, @Param("foodId") Long foodId);
    int insert(@Param("userId") Long userId, @Param("foodId") Long foodId, @Param("quantity") int quantity);
    int updateQuantity(@Param("userId") Long userId, @Param("foodId") Long foodId, @Param("quantity") int quantity);
    int deleteItem(@Param("userId") Long userId, @Param("foodId") Long foodId);
    int deleteByUserId(@Param("userId") Long userId);
}
