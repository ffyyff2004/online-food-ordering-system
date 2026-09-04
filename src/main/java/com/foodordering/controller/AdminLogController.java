package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.entity.OperationLog;
import com.foodordering.service.OperationLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/logs")
public class AdminLogController {
    private final OperationLogService logService;

    public AdminLogController(OperationLogService logService) { this.logService = logService; }

    @GetMapping
    public ApiResponse<List<OperationLog>> list(@RequestParam(defaultValue = "50") int limit) {
        return ApiResponse.ok(logService.list(limit));
    }
}
