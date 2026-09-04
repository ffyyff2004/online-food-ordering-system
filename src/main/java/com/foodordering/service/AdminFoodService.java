package com.foodordering.service;

import com.foodordering.dto.FoodRequest;
import com.foodordering.entity.Food;
import com.foodordering.mapper.AdminCategoryMapper;
import com.foodordering.mapper.AdminFoodMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminFoodService {
    private final AdminFoodMapper foodMapper;
    private final AdminCategoryMapper categoryMapper;
    private final OperationLogService logService;

    public AdminFoodService(AdminFoodMapper foodMapper, AdminCategoryMapper categoryMapper, OperationLogService logService) {
        this.foodMapper = foodMapper;
        this.categoryMapper = categoryMapper;
        this.logService = logService;
    }

    public List<Food> list(String keyword, Long categoryId) {
        return foodMapper.findAll(keyword, categoryId);
    }

    @Transactional
    public void create(FoodRequest request) {
        validateCategory(request.categoryId());
        foodMapper.insert(request);
        logService.recordCurrentAdmin("新增菜品", "POST", "/admin/foods", request.name());
    }

    @Transactional
    public void update(Long id, FoodRequest request) {
        if (foodMapper.findById(id) == null) throw new IllegalArgumentException("菜品不存在");
        validateCategory(request.categoryId());
        foodMapper.update(id, request);
        logService.recordCurrentAdmin("修改菜品", "PUT", "/admin/foods/" + id, request.name());
    }

    @Transactional
    public void updateStatus(Long id, int status) {
        ensureFood(id);
        if (status != 0 && status != 1) throw new IllegalArgumentException("状态只能是0或1");
        foodMapper.updateStatus(id, status);
        logService.recordCurrentAdmin(status == 1 ? "菜品上架" : "菜品下架", "PUT", "/admin/foods/" + id + "/status", "status=" + status);
    }

    @Transactional
    public void updateStock(Long id, int stock) {
        ensureFood(id);
        if (stock < 0) throw new IllegalArgumentException("库存不能小于0");
        foodMapper.updateStock(id, stock);
        logService.recordCurrentAdmin("调整库存", "PUT", "/admin/foods/" + id + "/stock", "stock=" + stock);
    }

    private void validateCategory(Long categoryId) {
        if (categoryMapper.findById(categoryId) == null) throw new IllegalArgumentException("分类不存在");
    }

    private void ensureFood(Long id) {
        if (foodMapper.findById(id) == null) throw new IllegalArgumentException("菜品不存在");
    }
}
