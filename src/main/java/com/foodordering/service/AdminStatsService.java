package com.foodordering.service;

import com.foodordering.entity.DashboardStats;
import com.foodordering.mapper.AdminStatsMapper;
import org.springframework.stereotype.Service;

@Service
public class AdminStatsService {
    private final AdminStatsMapper statsMapper;

    public AdminStatsService(AdminStatsMapper statsMapper) { this.statsMapper = statsMapper; }

    public DashboardStats summary() {
        DashboardStats stats = statsMapper.summary();
        stats.setTopFoods(statsMapper.topFoods());
        return stats;
    }
}
