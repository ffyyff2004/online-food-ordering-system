package com.foodordering.controller;

import com.foodordering.common.ApiResponse;
import com.foodordering.dto.AddressRequest;
import com.foodordering.dto.UpdateProfileRequest;
import com.foodordering.entity.User;
import com.foodordering.entity.UserAddress;
import com.foodordering.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) { this.profileService = profileService; }

    @GetMapping
    public ApiResponse<User> profile(@AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(profileService.profile(userId));
    }

    @PutMapping
    public ApiResponse<Void> update(@AuthenticationPrincipal Long userId,
                                    @Valid @RequestBody UpdateProfileRequest request) {
        profileService.updateProfile(userId, request);
        return ApiResponse.ok("资料已更新", null);
    }

    @GetMapping("/addresses")
    public ApiResponse<List<UserAddress>> addresses(@AuthenticationPrincipal Long userId) {
        return ApiResponse.ok(profileService.addresses(userId));
    }

    @PostMapping("/addresses")
    public ApiResponse<Void> addAddress(@AuthenticationPrincipal Long userId,
                                        @Valid @RequestBody AddressRequest request) {
        profileService.addAddress(userId, request);
        return ApiResponse.ok("地址添加成功", null);
    }

    @PutMapping("/addresses/{id}")
    public ApiResponse<Void> updateAddress(@AuthenticationPrincipal Long userId,
                                            @PathVariable Long id,
                                            @Valid @RequestBody AddressRequest request) {
        profileService.updateAddress(userId, id, request);
        return ApiResponse.ok("地址已更新", null);
    }

    @DeleteMapping("/addresses/{id}")
    public ApiResponse<Void> deleteAddress(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        profileService.deleteAddress(userId, id);
        return ApiResponse.ok("地址已删除", null);
    }
}
