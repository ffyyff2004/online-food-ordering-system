package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.dto.NoticeRequest;
import com.foodordering.entity.Notice;
import com.foodordering.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/notices")
public class AdminNoticeController {
    private final NoticeService noticeService;

    public AdminNoticeController(NoticeService noticeService) { this.noticeService = noticeService; }

    @GetMapping
    public ApiResponse<List<Notice>> list() { return ApiResponse.ok(noticeService.all()); }

    @PostMapping
    public ApiResponse<Void> create(@Valid @RequestBody NoticeRequest request) {
        noticeService.create(request);
        return ApiResponse.ok("公告已发布", null);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody NoticeRequest request) {
        noticeService.update(id, request);
        return ApiResponse.ok("公告已更新", null);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam int status) {
        noticeService.updateStatus(id, status);
        return ApiResponse.ok("公告状态已更新", null);
    }
}
