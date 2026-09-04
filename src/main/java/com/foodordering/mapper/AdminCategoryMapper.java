package com.foodordering.mapper;

import com.foodordering.dto.CategoryRequest;
import com.foodordering.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminCategoryMapper {
    List<Category> findAll();
    Category findById(@Param("id") Long id);
    int insert(@Param("request") CategoryRequest request);
    int update(@Param("id") Long id, @Param("request") CategoryRequest request);
}
