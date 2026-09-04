package com.foodordering.service;

import com.foodordering.common.PageResult;
import com.foodordering.entity.Category;
import com.foodordering.entity.Food;
import com.foodordering.mapper.CategoryMapper;
import com.foodordering.mapper.FoodMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodService {
    private final CategoryMapper categoryMapper;
    private final FoodMapper foodMapper;

    public FoodService(CategoryMapper categoryMapper, FoodMapper foodMapper) {
        this.categoryMapper = categoryMapper;
        this.foodMapper = foodMapper;
    }

    public List<Category> categories() { return categoryMapper.findEnabled(); }

    public PageResult<Food> foods(Long categoryId, String keyword, int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 50);
        int offset = (safePage - 1) * safeSize;
        return new PageResult<>(foodMapper.findPage(categoryId, keyword, offset, safeSize),
                foodMapper.count(categoryId, keyword), safePage, safeSize);
    }

    public Food food(Long id) {
        Food food = foodMapper.findById(id);
        if (food == null) throw new IllegalArgumentException("菜品不存在");
        return food;
    }
}
