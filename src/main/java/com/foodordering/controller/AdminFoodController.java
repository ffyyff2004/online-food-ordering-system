package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.dto.FoodRequest;
import com.foodordering.entity.Food;
import com.foodordering.service.AdminFoodService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/foods")
public class AdminFoodController {
    private final AdminFoodService foodService;

    public AdminFoodController(AdminFoodService foodService) { this.foodService = foodService; }

    @GetMapping
    public ApiResponse<List<Food>> list(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Long categoryId) {
        return ApiResponse.ok(foodService.list(keyword, categoryId));
    }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody FoodRequest request) {
        foodService.create(request);
        return ApiResponse.ok("菜品创建成功", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody FoodRequest request) {
        foodService.update(id, request);
        return ApiResponse.ok("菜品更新成功", null);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam int status) {
        foodService.updateStatus(id, status);
        return ApiResponse.ok("菜品状态已更新", null);
    }

    @PutMapping("/{id}/stock")
    public ApiResponse<Void> updateStock(@PathVariable Long id, @RequestParam int stock) {
        foodService.updateStock(id, stock);
        return ApiResponse.ok("库存已更新", null);
    }
}
