package com.foodordering.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrderRequest(
        @NotBlank(message = "收货地址不能为空") @Size(max = 500, message = "收货地址不能超过500个字符") String addressSnapshot
) {}
