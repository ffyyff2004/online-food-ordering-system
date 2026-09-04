package com.foodordering.service;

import com.foodordering.dto.AddCartItemRequest;
import com.foodordering.dto.UpdateCartItemRequest;
import com.foodordering.entity.CartItem;
import com.foodordering.entity.Food;
import com.foodordering.mapper.CartMapper;
import com.foodordering.mapper.FoodMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {
    private final CartMapper cartMapper;
    private final FoodMapper foodMapper;

    public CartService(CartMapper cartMapper, FoodMapper foodMapper) {
        this.cartMapper = cartMapper;
        this.foodMapper = foodMapper;
    }

    public List<CartItem> list(Long userId) { return cartMapper.findByUserId(userId); }

    @Transactional
    public void add(Long userId, AddCartItemRequest request) {
        Food food = foodMapper.findById(request.foodId());
        if (food == null) throw new IllegalArgumentException("菜品不存在或已下架");
        CartItem existing = cartMapper.findByUserAndFood(userId, request.foodId());
        int quantity = request.quantity() + (existing == null ? 0 : existing.getQuantity());
        if (quantity > food.getStock()) throw new IllegalArgumentException("菜品库存不足");
        if (existing == null) cartMapper.insert(userId, request.foodId(), quantity);
        else cartMapper.updateQuantity(userId, request.foodId(), quantity);
    }

    @Transactional
    public void update(Long userId, Long foodId, UpdateCartItemRequest request) {
        Food food = foodMapper.findById(foodId);
        if (food == null) throw new IllegalArgumentException("菜品不存在或已下架");
        if (request.quantity() > food.getStock()) throw new IllegalArgumentException("菜品库存不足");
        if (cartMapper.updateQuantity(userId, foodId, request.quantity()) == 0) {
            throw new IllegalArgumentException("购物车中没有该菜品");
        }
    }

    @Transactional
    public void remove(Long userId, Long foodId) { cartMapper.deleteItem(userId, foodId); }
}
