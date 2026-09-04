package com.foodordering.dto;

public record AdminLoginResponse(Long adminId, String username, String realName, String token) {}
