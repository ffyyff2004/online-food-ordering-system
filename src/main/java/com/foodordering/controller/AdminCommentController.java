package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.entity.FoodComment;
import com.foodordering.service.CommentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
public class AdminCommentController {
    private final CommentService commentService;

    public AdminCommentController(CommentService commentService) { this.commentService = commentService; }

    @GetMapping
    public ApiResponse<List<FoodComment>> list() { return ApiResponse.ok(commentService.all()); }
}
