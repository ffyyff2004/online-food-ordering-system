package com.foodordering.service;

import com.foodordering.dto.CategoryRequest;
import com.foodordering.entity.Category;
import com.foodordering.mapper.AdminCategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminCategoryService {
    private final AdminCategoryMapper categoryMapper;
    private final OperationLogService logService;

    public AdminCategoryService(AdminCategoryMapper categoryMapper, OperationLogService logService) {
        this.categoryMapper = categoryMapper;
        this.logService = logService;
    }

    public List<Category> list() { return categoryMapper.findAll(); }

    @Transactional
    public void create(CategoryRequest request) {
        categoryMapper.insert(request);
        logService.recordCurrentAdmin("新增分类", "POST", "/admin/categories", request.name());
    }

    @Transactional
    public void update(Long id, CategoryRequest request) {
        if (categoryMapper.findById(id) == null) throw new IllegalArgumentException("分类不存在");
        categoryMapper.update(id, request);
        logService.recordCurrentAdmin("修改分类", "PUT", "/admin/categories/" + id, request.name());
    }
}
