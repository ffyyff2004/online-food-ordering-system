package com.foodordering.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoticeRequest(
        @NotBlank(message = "公告标题不能为空") @Size(max = 100, message = "公告标题不能超过100个字符") String title,
        @NotBlank(message = "公告内容不能为空") @Size(max = 5000, message = "公告内容不能超过5000个字符") String content,
        Integer status
) {}
