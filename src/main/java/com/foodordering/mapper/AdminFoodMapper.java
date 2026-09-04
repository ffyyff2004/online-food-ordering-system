package com.foodordering.mapper;

import com.foodordering.dto.FoodRequest;
import com.foodordering.entity.Food;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminFoodMapper {
    List<Food> findAll(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);
    Food findById(@Param("id") Long id);
    int insert(@Param("request") FoodRequest request);
    int update(@Param("id") Long id, @Param("request") FoodRequest request);
    int updateStatus(@Param("id") Long id, @Param("status") int status);
    int updateStock(@Param("id") Long id, @Param("stock") int stock);
}
