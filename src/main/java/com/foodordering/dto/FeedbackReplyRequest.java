package com.foodordering.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedbackReplyRequest(@NotBlank(message = "回复内容不能为空") @Size(max = 1000) String reply) {}
