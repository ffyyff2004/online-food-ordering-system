package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.common.PageResult;
import com.foodordering.entity.Category;
import com.foodordering.entity.Food;
import com.foodordering.service.FoodService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/foods")
public class FoodController {
    private final FoodService foodService;

    public FoodController(FoodService foodService) { this.foodService = foodService; }

    @GetMapping("/categories")
    public ApiResponse<List<Category>> categories() { return ApiResponse.ok(foodService.categories()); }

    @GetMapping
    public ApiResponse<PageResult<Food>> foods(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(foodService.foods(categoryId, keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<Food> food(@PathVariable Long id) { return ApiResponse.ok(foodService.food(id)); }
}
