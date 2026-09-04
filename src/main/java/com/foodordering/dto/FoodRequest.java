package com.foodordering.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record FoodRequest(
        @NotNull(message = "分类不能为空") Long categoryId,
        @NotBlank(message = "菜品名称不能为空") @Size(max = 100, message = "菜品名称不能超过100个字符") String name,
        @Size(max = 500, message = "描述不能超过500个字符") String description,
        @NotNull(message = "价格不能为空") @DecimalMin(value = "0.01", message = "价格必须大于0") BigDecimal price,
        BigDecimal originalPrice,
        @Size(max = 500, message = "图片地址不能超过500个字符") String imageUrl,
        @NotNull(message = "库存不能为空") @Min(value = 0, message = "库存不能小于0") Integer stock,
        @Min(0) @Max(1) Integer recommended,
        @Min(0) @Max(1) Integer specialOffer,
        @Min(0) @Max(1) Integer status
) {}
