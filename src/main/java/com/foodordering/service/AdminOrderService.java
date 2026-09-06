package com.foodordering.service;

import com.foodordering.entity.Order;
import com.foodordering.mapper.AdminOrderMapper;
import com.foodordering.mapper.FoodMapper;
import com.foodordering.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AdminOrderService {
    private static final Set<String> STATUSES = Set.of(
            "PENDING_PAYMENT", "PAID", "PREPARING", "READY", "COMPLETED", "CANCELLED");
    private static final Map<String, Set<String>> TRANSITIONS = Map.of(
            "PENDING_PAYMENT", Set.of("CANCELLED"),
            "PAID", Set.of("PREPARING", "CANCELLED"),
            "PREPARING", Set.of("READY", "CANCELLED"),
            "READY", Set.of("COMPLETED", "CANCELLED"),
            "COMPLETED", Set.of(),
            "CANCELLED", Set.of());
    private final AdminOrderMapper adminOrderMapper;
    private final OrderMapper orderMapper;
    private final FoodMapper foodMapper;
    private final OperationLogService logService;

    public AdminOrderService(AdminOrderMapper adminOrderMapper, OrderMapper orderMapper, FoodMapper foodMapper, OperationLogService logService) {
        this.adminOrderMapper = adminOrderMapper;
        this.orderMapper = orderMapper;
        this.foodMapper = foodMapper;
        this.logService = logService;
    }

    public List<Order> list(String status) {
        List<Order> orders = adminOrderMapper.findAll(status);
        orders.forEach(order -> order.setItems(orderMapper.findItemsByOrderId(order.getId())));
        return orders;
    }

    public Order detail(String orderNo) {
        Order order = adminOrderMapper.findByOrderNo(orderNo);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        order.setItems(orderMapper.findItemsByOrderId(order.getId()));
        return order;
    }

    @Transactional
    public void updateStatus(String orderNo, String status) {
        if (!STATUSES.contains(status)) throw new IllegalArgumentException("订单状态不合法");
        Order order = adminOrderMapper.findByOrderNoForUpdate(orderNo);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        if (order.getStatus().equals(status)) return;
        if (!TRANSITIONS.getOrDefault(order.getStatus(), Set.of()).contains(status)) {
            throw new IllegalArgumentException("订单不能从 " + order.getStatus() + " 变更为 " + status);
        }
        if (adminOrderMapper.updateStatus(orderNo, status) == 0) {
            throw new IllegalArgumentException("订单不存在");
        }
        if ("CANCELLED".equals(status)) {
            for (var item : orderMapper.findItemsByOrderId(order.getId())) {
                foodMapper.restoreStock(item.getFoodId(), item.getQuantity());
            }
        }
        logService.recordCurrentAdmin("修改订单状态", "PUT", "/admin/orders/" + orderNo + "/status", "status=" + status);
    }
}
