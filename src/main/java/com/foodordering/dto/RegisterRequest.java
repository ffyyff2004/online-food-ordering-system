package com.foodordering.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "用户名不能为空") @Size(min = 3, max = 30, message = "用户名长度应为3到30位") String username,
        @NotBlank(message = "密码不能为空") @Size(min = 6, max = 100, message = "密码至少6位") String password,
        @Size(max = 30, message = "昵称不能超过30个字符") String nickname,
        @Size(max = 20, message = "手机号不能超过20个字符") String phone
) {}
