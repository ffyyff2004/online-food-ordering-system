package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.entity.Order;
import com.foodordering.service.AdminOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
public class AdminOrderController {
    private final AdminOrderService orderService;

    public AdminOrderController(AdminOrderService orderService) { this.orderService = orderService; }

    @GetMapping
    public ApiResponse<List<Order>> list(@RequestParam(required = false) String status) {
        return ApiResponse.ok(orderService.list(status));
    }

    @PutMapping("/{orderNo}/status")
    public ApiResponse<Void> updateStatus(@PathVariable String orderNo, @RequestParam String status) {
        orderService.updateStatus(orderNo, status);
        return ApiResponse.ok("订单状态已更新", null);
    }
}
