package com.foodordering.entity;

import java.math.BigDecimal;
import java.util.List;

public class DashboardStats {
    private long userCount;
    private long foodCount;
    private long orderCount;
    private long pendingOrderCount;
    private long todayOrderCount;
    private BigDecimal totalRevenue = BigDecimal.ZERO;
    private BigDecimal todayRevenue = BigDecimal.ZERO;
    private List<TopFoodStat> topFoods;

    public long getUserCount() { return userCount; }
    public void setUserCount(long userCount) { this.userCount = userCount; }
    public long getFoodCount() { return foodCount; }
    public void setFoodCount(long foodCount) { this.foodCount = foodCount; }
    public long getOrderCount() { return orderCount; }
    public void setOrderCount(long orderCount) { this.orderCount = orderCount; }
    public long getPendingOrderCount() { return pendingOrderCount; }
    public void setPendingOrderCount(long pendingOrderCount) { this.pendingOrderCount = pendingOrderCount; }
    public long getTodayOrderCount() { return todayOrderCount; }
    public void setTodayOrderCount(long todayOrderCount) { this.todayOrderCount = todayOrderCount; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }
    public List<TopFoodStat> getTopFoods() { return topFoods; }
    public void setTopFoods(List<TopFoodStat> topFoods) { this.topFoods = topFoods; }
}
