package com.foodordering.mapper;

import com.foodordering.entity.FoodComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<FoodComment> findByFoodId(@Param("foodId") Long foodId);
    List<FoodComment> findAll();
    int countByUserOrderFood(@Param("userId") Long userId, @Param("orderId") Long orderId, @Param("foodId") Long foodId);
    int insert(@Param("userId") Long userId, @Param("foodId") Long foodId, @Param("orderId") Long orderId,
               @Param("rating") int rating, @Param("content") String content);
}
