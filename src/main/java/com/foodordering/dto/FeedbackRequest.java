package com.foodordering.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedbackRequest(@NotBlank(message = "反馈内容不能为空") @Size(max = 1000) String content) {}
