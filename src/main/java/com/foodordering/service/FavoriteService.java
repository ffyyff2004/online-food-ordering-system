package com.foodordering.service;

import com.foodordering.entity.Favorite;
import com.foodordering.mapper.FavoriteMapper;
import com.foodordering.mapper.FoodMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {
    private final FavoriteMapper favoriteMapper;
    private final FoodMapper foodMapper;

    public FavoriteService(FavoriteMapper favoriteMapper, FoodMapper foodMapper) {
        this.favoriteMapper = favoriteMapper;
        this.foodMapper = foodMapper;
    }

    public List<Favorite> list(Long userId) { return favoriteMapper.findByUserId(userId); }

    @Transactional
    public void add(Long userId, Long foodId) {
        if (foodMapper.findById(foodId) == null) throw new IllegalArgumentException("菜品不存在或已下架");
        if (favoriteMapper.count(userId, foodId) == 0) favoriteMapper.insert(userId, foodId);
    }

    @Transactional
    public void remove(Long userId, Long foodId) { favoriteMapper.delete(userId, foodId); }
}
