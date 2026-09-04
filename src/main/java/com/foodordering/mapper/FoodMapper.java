package com.foodordering.mapper;

import com.foodordering.entity.Food;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FoodMapper {
    List<Food> findPage(@Param("categoryId") Long categoryId,
                        @Param("keyword") String keyword,
                        @Param("offset") int offset,
                        @Param("size") int size);
    long count(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);
    Food findById(@Param("id") Long id);
    Food findByIdForUpdate(@Param("id") Long id);
    int decreaseStock(@Param("id") Long id, @Param("quantity") int quantity);
    int restoreStock(@Param("id") Long id, @Param("quantity") int quantity);
}
