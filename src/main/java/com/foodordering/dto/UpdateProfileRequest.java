package com.foodordering.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 30, message = "昵称不能超过30个字符") String nickname,
        @Size(max = 20, message = "手机号不能超过20个字符") String phone,
        @Size(max = 255, message = "头像地址不能超过255个字符") String avatar
) {}
