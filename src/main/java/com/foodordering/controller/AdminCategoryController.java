package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.dto.CategoryRequest;
import com.foodordering.entity.Category;
import com.foodordering.service.AdminCategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final AdminCategoryService categoryService;

    public AdminCategoryController(AdminCategoryService categoryService) { this.categoryService = categoryService; }

    @GetMapping
    public ApiResponse<List<Category>> list() { return ApiResponse.ok(categoryService.list()); }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody CategoryRequest request) {
        categoryService.create(request);
        return ApiResponse.ok("分类创建成功", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        categoryService.update(id, request);
        return ApiResponse.ok("分类更新成功", null);
    }
}
