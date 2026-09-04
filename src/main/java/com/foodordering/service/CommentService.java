package com.foodordering.service;

import com.foodordering.dto.CommentRequest;
import com.foodordering.entity.FoodComment;
import com.foodordering.mapper.CommentMapper;
import com.foodordering.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {
    private final CommentMapper commentMapper;
    private final OrderMapper orderMapper;

    public CommentService(CommentMapper commentMapper, OrderMapper orderMapper) {
        this.commentMapper = commentMapper;
        this.orderMapper = orderMapper;
    }

    public List<FoodComment> list(Long foodId) { return commentMapper.findByFoodId(foodId); }
    public List<FoodComment> all() { return commentMapper.findAll(); }

    @Transactional
    public void create(Long userId, CommentRequest request) {
        if (orderMapper.countCompletedItem(userId, request.orderId(), request.foodId()) == 0) {
            throw new IllegalArgumentException("只有完成订单中的菜品才能评价");
        }
        if (commentMapper.countByUserOrderFood(userId, request.orderId(), request.foodId()) > 0) {
            throw new IllegalArgumentException("该菜品已经评价过了");
        }
        commentMapper.insert(userId, request.foodId(), request.orderId(), request.rating(), request.content());
    }
}
