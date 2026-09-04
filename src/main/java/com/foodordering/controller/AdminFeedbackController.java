package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.dto.FeedbackReplyRequest;
import com.foodordering.entity.Feedback;
import com.foodordering.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/feedback")
public class AdminFeedbackController {
    private final FeedbackService feedbackService;

    public AdminFeedbackController(FeedbackService feedbackService) { this.feedbackService = feedbackService; }

    @GetMapping
    public ApiResponse<List<Feedback>> list(@RequestParam(required = false) String status) {
        return ApiResponse.ok(feedbackService.all(status));
    }

    @PutMapping("/{id}/reply")
    public ApiResponse<Void> reply(@PathVariable Long id, @Valid @RequestBody FeedbackReplyRequest request) {
        feedbackService.reply(id, request);
        return ApiResponse.ok("反馈已回复", null);
    }
}
