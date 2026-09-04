package com.foodordering.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotNull Long foodId,
        @NotNull Long orderId,
        @NotNull @Min(1) @Max(5) Integer rating,
        @NotBlank @Size(max = 500) String content
) {}
