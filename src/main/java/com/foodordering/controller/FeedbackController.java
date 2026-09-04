package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.dto.FeedbackRequest;
import com.foodordering.entity.Feedback;
import com.foodordering.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) { this.feedbackService = feedbackService; }

    @PostMapping
    public ApiResponse<Void> create(@AuthenticationPrincipal Long userId,
                                    @Valid @RequestBody FeedbackRequest request) {
        feedbackService.create(userId, request);
        return ApiResponse.ok("反馈已提交", null);
    }

    @GetMapping
    public ApiResponse<List<Feedback>> mine(@AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(feedbackService.mine(userId));
    }
}
