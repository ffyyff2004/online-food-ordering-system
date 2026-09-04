package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.dto.CommentRequest;
import com.foodordering.entity.FoodComment;
import com.foodordering.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) { this.commentService = commentService; }

    @GetMapping("/food/{foodId}")
    public ApiResponse<List<FoodComment>> list(@PathVariable Long foodId) {
        return ApiResponse.ok(commentService.list(foodId));
    }

    @PostMapping
    public ApiResponse<Void> create(@AuthenticationPrincipal Long userId,
                                    @Valid @RequestBody CommentRequest request) {
        commentService.create(userId, request);
        return ApiResponse.ok("评价成功", null);
    }
}
