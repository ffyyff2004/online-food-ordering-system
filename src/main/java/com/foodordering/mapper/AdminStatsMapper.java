package com.foodordering.mapper;

import com.foodordering.entity.DashboardStats;
import com.foodordering.entity.TopFoodStat;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminStatsMapper {
    DashboardStats summary();
    List<TopFoodStat> topFoods();
}
