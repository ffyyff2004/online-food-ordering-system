package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.entity.DashboardStats;
import com.foodordering.service.AdminStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/stats")
public class AdminStatsController {
    private final AdminStatsService statsService;

    public AdminStatsController(AdminStatsService statsService) { this.statsService = statsService; }

    @GetMapping("/summary")
    public ApiResponse<DashboardStats> summary() { return ApiResponse.ok(statsService.summary()); }
}
