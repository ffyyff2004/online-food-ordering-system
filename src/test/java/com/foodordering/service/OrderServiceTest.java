package com.foodordering.service;

import com.foodordering.dto.CreateOrderRequest;
import com.foodordering.entity.CartItem;
import com.foodordering.entity.Food;
import com.foodordering.entity.Order;
import com.foodordering.entity.OrderItem;
import com.foodordering.mapper.CartMapper;
import com.foodordering.mapper.FoodMapper;
import com.foodordering.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.Answer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock CartMapper cartMapper;
    @Mock FoodMapper foodMapper;
    @Mock OrderMapper orderMapper;
    @InjectMocks OrderService orderService;

    @Test
    void createOrderCalculatesTotalDecreasesStockAndClearsCart() {
        CartItem cartItem = new CartItem();
        cartItem.setFoodId(1L);
        cartItem.setQuantity(2);
        Food food = food(1L, "番茄鸡蛋面", "18.00", 10);
        when(cartMapper.findByUserId(7L)).thenReturn(List.of(cartItem));
        when(foodMapper.findByIdForUpdate(1L)).thenReturn(food);
        when(foodMapper.decreaseStock(1L, 2)).thenReturn(1);
        when(orderMapper.insert(any(Order.class))).thenAnswer((Answer<Integer>) invocation -> {
            invocation.getArgument(0, Order.class).setId(99L);
            return 1;
        });
        when(orderMapper.findItemsByOrderId(99L)).thenReturn(List.of());

        Order order = orderService.create(7L, new CreateOrderRequest("测试地址"));

        assertEquals(new BigDecimal("36.00"), order.getTotalAmount());
        assertEquals("PENDING_PAYMENT", order.getStatus());
        verify(foodMapper).decreaseStock(1L, 2);
        verify(orderMapper).insertItem(any());
        verify(cartMapper).deleteByUserId(7L);
    }

    @Test
    void createOrderRejectsInsufficientStockBeforeInsert() {
        CartItem cartItem = new CartItem();
        cartItem.setFoodId(1L);
        cartItem.setQuantity(11);
        when(cartMapper.findByUserId(7L)).thenReturn(List.of(cartItem));
        when(foodMapper.findByIdForUpdate(1L)).thenReturn(food(1L, "番茄鸡蛋面", "18.00", 10));

        assertThrows(IllegalArgumentException.class,
                () -> orderService.create(7L, new CreateOrderRequest("测试地址")));

        verify(orderMapper, never()).insert(any(Order.class));
        verify(foodMapper, never()).decreaseStock(eq(1L), eq(11));
        verify(cartMapper, never()).deleteByUserId(7L);
    }

    @Test
    void cancelOrderRestoresStock() {
        Order order = new Order();
        order.setId(99L);
        order.setOrderNo("ORDER-1");
        order.setStatus("PAID");
        OrderItem item = new OrderItem();
        item.setFoodId(1L);
        item.setQuantity(2);
        when(orderMapper.findByOrderNoForUpdate(7L, "ORDER-1")).thenReturn(order);
        when(orderMapper.cancel(7L, "ORDER-1", "PAID")).thenReturn(1);
        when(orderMapper.findItemsByOrderId(99L)).thenReturn(List.of(item));
        when(foodMapper.restoreStock(1L, 2)).thenReturn(1);

        orderService.cancel(7L, "ORDER-1");

        verify(foodMapper).restoreStock(1L, 2);
        verify(orderMapper).cancel(7L, "ORDER-1", "PAID");
    }

    @Test
    void cancelOrderRejectsCompletedOrder() {
        Order order = new Order();
        order.setId(99L);
        order.setStatus("COMPLETED");
        when(orderMapper.findByOrderNoForUpdate(7L, "ORDER-1")).thenReturn(order);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.cancel(7L, "ORDER-1"));

        verify(orderMapper, never()).cancel(any(), any(), any());
        verify(foodMapper, never()).restoreStock(any(), anyInt());
    }

    private Food food(Long id, String name, String price, int stock) {
        Food food = new Food();
        food.setId(id);
        food.setName(name);
        food.setPrice(new BigDecimal(price));
        food.setStock(stock);
        return food;
    }
}
