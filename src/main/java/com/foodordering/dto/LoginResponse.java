package com.foodordering.dto;

public record LoginResponse(Long userId, String username, String nickname, String token) {}
