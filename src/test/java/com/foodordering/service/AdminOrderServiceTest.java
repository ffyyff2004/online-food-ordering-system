package com.foodordering.service;

import com.foodordering.entity.Order;
import com.foodordering.mapper.AdminOrderMapper;
import com.foodordering.mapper.FoodMapper;
import com.foodordering.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminOrderServiceTest {
    @Mock AdminOrderMapper adminOrderMapper;
    @Mock OrderMapper orderMapper;
    @Mock FoodMapper foodMapper;
    @Mock OperationLogService logService;
    @InjectMocks AdminOrderService adminOrderService;

    @Test
    void allowsOnlyNextStatus() {
        Order order = order("PAID");
        when(adminOrderMapper.findByOrderNoForUpdate("ORDER-1")).thenReturn(order);
        when(adminOrderMapper.updateStatus("ORDER-1", "PREPARING")).thenReturn(1);

        adminOrderService.updateStatus("ORDER-1", "PREPARING");

        verify(adminOrderMapper).updateStatus("ORDER-1", "PREPARING");
    }

    @Test
    void rejectsSkippingStatus() {
        when(adminOrderMapper.findByOrderNoForUpdate("ORDER-1")).thenReturn(order("PAID"));

        assertThrows(IllegalArgumentException.class,
                () -> adminOrderService.updateStatus("ORDER-1", "COMPLETED"));

        verify(adminOrderMapper, never()).updateStatus("ORDER-1", "COMPLETED");
    }

    @Test
    void rejectsChangingCompletedOrder() {
        when(adminOrderMapper.findByOrderNoForUpdate("ORDER-1")).thenReturn(order("COMPLETED"));

        assertThrows(IllegalArgumentException.class,
                () -> adminOrderService.updateStatus("ORDER-1", "CANCELLED"));

        verify(adminOrderMapper, never()).updateStatus("ORDER-1", "CANCELLED");
    }

    private Order order(String status) {
        Order order = new Order();
        order.setOrderNo("ORDER-1");
        order.setStatus(status);
        return order;
    }
}
