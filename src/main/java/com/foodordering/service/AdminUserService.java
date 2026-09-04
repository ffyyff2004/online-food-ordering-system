package com.foodordering.service;

import com.foodordering.entity.AdminUserView;
import com.foodordering.mapper.AdminUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserService {
    private final AdminUserMapper userMapper;
    private final OperationLogService logService;

    public AdminUserService(AdminUserMapper userMapper, OperationLogService logService) {
        this.userMapper = userMapper;
        this.logService = logService;
    }

    public List<AdminUserView> list(String keyword, Integer status) {
        return userMapper.findAll(keyword, status);
    }

    @Transactional
    public void updateStatus(Long id, int status) {
        if (status != 0 && status != 1) throw new IllegalArgumentException("状态只能是0或1");
        if (userMapper.updateStatus(id, status) == 0) throw new IllegalArgumentException("用户不存在");
        logService.recordCurrentAdmin(status == 1 ? "启用用户" : "停用用户", "PUT", "/admin/users/" + id + "/status", "status=" + status);
    }
}
