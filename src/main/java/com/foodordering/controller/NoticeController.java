package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.entity.Notice;
import com.foodordering.service.NoticeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notices")
public class NoticeController {
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) { this.noticeService = noticeService; }

    @GetMapping
    public ApiResponse<List<Notice>> enabled() { return ApiResponse.ok(noticeService.enabled()); }
}
