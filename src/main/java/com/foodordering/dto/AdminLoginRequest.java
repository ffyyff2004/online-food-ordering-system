package com.foodordering.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequest(
        @NotBlank(message = "管理员用户名不能为空") String username,
        @NotBlank(message = "管理员密码不能为空") String password
) {}
