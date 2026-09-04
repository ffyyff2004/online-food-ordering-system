package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.dto.AddCartItemRequest;
import com.foodordering.dto.UpdateCartItemRequest;
import com.foodordering.entity.CartItem;
import com.foodordering.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) { this.cartService = cartService; }

    @GetMapping
    public ApiResponse<List<CartItem>> list(@AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(cartService.list(userId));
    }

    @PostMapping("/items")
    public ApiResponse<Void> add(@AuthenticationPrincipal Long userId,
                                 @Valid @RequestBody AddCartItemRequest request) {
        cartService.add(userId, request);
        return ApiResponse.ok("已加入购物车", null);
    }

    @PutMapping("/items/{foodId}")
    public ApiResponse<Void> update(@AuthenticationPrincipal Long userId,
                                    @PathVariable Long foodId,
                                    @Valid @RequestBody UpdateCartItemRequest request) {
        cartService.update(userId, foodId, request);
        return ApiResponse.ok("购物车已更新", null);
    }

    @DeleteMapping("/items/{foodId}")
    public ApiResponse<Void> remove(@AuthenticationPrincipal Long userId, @PathVariable Long foodId) {
        cartService.remove(userId, foodId);
        return ApiResponse.ok("已移出购物车", null);
    }
}
