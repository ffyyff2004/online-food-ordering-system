package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.entity.Favorite;
import com.foodordering.service.FavoriteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) { this.favoriteService = favoriteService; }

    @GetMapping
    public ApiResponse<List<Favorite>> list(@AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(favoriteService.list(userId));
    }

    @PostMapping("/{foodId}")
    public ApiResponse<Void> add(@AuthenticationPrincipal Long userId, @PathVariable Long foodId) {
        favoriteService.add(userId, foodId);
        return ApiResponse.ok("收藏成功", null);
    }

    @DeleteMapping("/{foodId}")
    public ApiResponse<Void> remove(@AuthenticationPrincipal Long userId, @PathVariable Long foodId) {
        favoriteService.remove(userId, foodId);
        return ApiResponse.ok("已取消收藏", null);
    }
}
