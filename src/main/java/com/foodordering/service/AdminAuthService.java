package com.foodordering.service;

import com.foodordering.dto.AdminLoginRequest;
import com.foodordering.dto.AdminLoginResponse;
import com.foodordering.entity.Admin;
import com.foodordering.mapper.AdminMapper;
import com.foodordering.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthService {
    private final AdminMapper adminMapper;
    private final JwtService jwtService;
    private final OperationLogService logService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminAuthService(AdminMapper adminMapper, JwtService jwtService, OperationLogService logService) {
        this.adminMapper = adminMapper;
        this.jwtService = jwtService;
        this.logService = logService;
    }

    public AdminLoginResponse login(AdminLoginRequest request) {
        Admin admin = adminMapper.findByUsername(request.username());
        if (admin == null || !passwordEncoder.matches(request.password(), admin.getPassword())) {
            throw new IllegalArgumentException("管理员用户名或密码错误");
        }
        if (!Integer.valueOf(1).equals(admin.getStatus())) {
            throw new IllegalArgumentException("管理员账号已停用");
        }
        logService.record(admin.getId(), "管理员登录", "POST", "/admin/auth/login", "登录成功");
        return new AdminLoginResponse(admin.getId(), admin.getUsername(), admin.getRealName(), jwtService.createToken(admin));
    }
}
