package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.dto.CreateOrderRequest;
import com.foodordering.entity.Order;
import com.foodordering.service.OrderService;
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
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public ApiResponse<Order> create(@AuthenticationPrincipal Long userId,
                                     @Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.ok("订单创建成功", orderService.create(userId, request));
    }

    @GetMapping
    public ApiResponse<List<Order>> list(@AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(orderService.list(userId));
    }

    @GetMapping("/{orderNo}")
    public ApiResponse<Order> detail(@AuthenticationPrincipal Long userId, @PathVariable String orderNo) {
        return ApiResponse.ok(orderService.detail(userId, orderNo));
    }

    @PostMapping("/{orderNo}/pay")
    public ApiResponse<Void> pay(@AuthenticationPrincipal Long userId, @PathVariable String orderNo) {
        orderService.pay(userId, orderNo);
        return ApiResponse.ok("模拟支付成功", null);
    }

    @PostMapping("/{orderNo}/cancel")
    public ApiResponse<Void> cancel(@AuthenticationPrincipal Long userId, @PathVariable String orderNo) {
        orderService.cancel(userId, orderNo);
        return ApiResponse.ok("订单已取消，库存已恢复", null);
    }
}
