package com.foodordering.service;

import com.foodordering.dto.CreateOrderRequest;
import com.foodordering.entity.CartItem;
import com.foodordering.entity.Food;
import com.foodordering.entity.Order;
import com.foodordering.entity.OrderItem;
import com.foodordering.mapper.CartMapper;
import com.foodordering.mapper.FoodMapper;
import com.foodordering.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class OrderService {
    private final CartMapper cartMapper;
    private final FoodMapper foodMapper;
    private final OrderMapper orderMapper;

    public OrderService(CartMapper cartMapper, FoodMapper foodMapper, OrderMapper orderMapper) {
        this.cartMapper = cartMapper;
        this.foodMapper = foodMapper;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public Order create(Long userId, CreateOrderRequest request) {
        List<CartItem> cartItems = cartMapper.findByUserId(userId);
        if (cartItems.isEmpty()) throw new IllegalArgumentException("购物车为空");

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStatus("PENDING_PAYMENT");
        order.setAddressSnapshot(request.addressSnapshot());
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Food food = foodMapper.findByIdForUpdate(cartItem.getFoodId());
            if (food == null) throw new IllegalArgumentException("购物车中存在已下架菜品");
            if (food.getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("菜品库存不足：" + food.getName());
            }
            BigDecimal subtotal = food.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            OrderItem item = new OrderItem();
            item.setFoodId(food.getId());
            item.setFoodNameSnapshot(food.getName());
            item.setPurchasePrice(food.getPrice());
            item.setQuantity(cartItem.getQuantity());
            item.setSubtotal(subtotal);
            items.add(item);
            total = total.add(subtotal);
        }
        order.setTotalAmount(total);
        orderMapper.insert(order);
        for (OrderItem item : items) {
            if (foodMapper.decreaseStock(item.getFoodId(), item.getQuantity()) != 1) {
                throw new IllegalArgumentException("扣减库存失败：" + item.getFoodNameSnapshot());
            }
            item.setOrderId(order.getId());
            orderMapper.insertItem(item);
        }
        cartMapper.deleteByUserId(userId);
        order.setItems(orderMapper.findItemsByOrderId(order.getId()));
        return order;
    }

    public List<Order> list(Long userId) {
        List<Order> orders = orderMapper.findByUserId(userId);
        orders.forEach(order -> order.setItems(orderMapper.findItemsByOrderId(order.getId())));
        return orders;
    }

    public Order detail(Long userId, String orderNo) {
        Order order = orderMapper.findByOrderNo(userId, orderNo);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        order.setItems(orderMapper.findItemsByOrderId(order.getId()));
        return order;
    }

    @Transactional
    public void pay(Long userId, String orderNo) {
        if (orderMapper.pay(userId, orderNo) == 0) {
            throw new IllegalArgumentException("订单不存在或当前状态不能支付");
        }
    }

    @Transactional
    public void cancel(Long userId, String orderNo) {
        Order order = orderMapper.findByOrderNoForUpdate(userId, orderNo);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        if (!Set.of("PENDING_PAYMENT", "PAID", "PREPARING", "READY").contains(order.getStatus())) {
            throw new IllegalArgumentException("当前订单状态不能取消");
        }
        if (orderMapper.cancel(userId, orderNo, order.getStatus()) != 1) {
            throw new IllegalArgumentException("订单取消失败");
        }
        for (OrderItem item : orderMapper.findItemsByOrderId(order.getId())) {
            foodMapper.restoreStock(item.getFoodId(), item.getQuantity());
        }
    }

    private String generateOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
