package com.foodordering.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank(message = "收货人不能为空") @Size(max = 30) String receiver,
        @NotBlank(message = "手机号不能为空") @Size(max = 20) String phone,
        @NotBlank(message = "详细地址不能为空") @Size(max = 255) String detail,
        Integer isDefault
) {}
