package com.foodordering.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemRequest(
        @NotNull(message = "数量不能为空") @Min(value = 1, message = "数量至少为1") @Max(value = 99, message = "数量不能超过99") Integer quantity
) {}
