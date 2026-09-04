package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.entity.AdminUserView;
import com.foodordering.service.AdminUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {
    private final AdminUserService userService;

    public AdminUserController(AdminUserService userService) { this.userService = userService; }

    @GetMapping
    public ApiResponse<List<AdminUserView>> list(@RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) Integer status) {
        return ApiResponse.ok(userService.list(keyword, status));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam int status) {
        userService.updateStatus(id, status);
        return ApiResponse.ok("用户状态已更新", null);
    }
}
