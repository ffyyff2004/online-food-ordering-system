package com.foodordering.mapper;

import com.foodordering.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> findEnabled();
}
