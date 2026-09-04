package com.foodordering.service;

import com.foodordering.entity.OperationLog;
import com.foodordering.mapper.OperationLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
public class OperationLogService {
    private final OperationLogMapper logMapper;

    public OperationLogService(OperationLogMapper logMapper) { this.logMapper = logMapper; }

    public void record(Long adminId, String action, String method, String path, String detail) {
        logMapper.insert(adminId, action, method, path, detail);
    }

    public void recordCurrentAdmin(String action, String method, String path, String detail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = null;
        if (authentication != null && authentication.getPrincipal() instanceof Number number
                && authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            adminId = number.longValue();
        }
        record(adminId, action, method, path, detail);
    }

    public List<OperationLog> list(int limit) {
        return logMapper.findAll(Math.min(Math.max(limit, 1), 200));
    }
}
