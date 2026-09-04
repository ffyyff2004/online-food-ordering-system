package com.foodordering.service;

import com.foodordering.dto.AddressRequest;
import com.foodordering.dto.UpdateProfileRequest;
import com.foodordering.entity.User;
import com.foodordering.entity.UserAddress;
import com.foodordering.mapper.AddressMapper;
import com.foodordering.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfileService {
    private final UserMapper userMapper;
    private final AddressMapper addressMapper;

    public ProfileService(UserMapper userMapper, AddressMapper addressMapper) {
        this.userMapper = userMapper;
        this.addressMapper = addressMapper;
    }

    public User profile(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) throw new IllegalArgumentException("用户不存在");
        user.setPassword(null);
        return user;
    }

    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userMapper.findById(userId);
        if (user == null) throw new IllegalArgumentException("用户不存在");
        String nickname = request.nickname() == null || request.nickname().isBlank() ? user.getNickname() : request.nickname();
        userMapper.updateProfile(userId, nickname, request.phone(), request.avatar());
    }

    public List<UserAddress> addresses(Long userId) { return addressMapper.findByUserId(userId); }

    @Transactional
    public void addAddress(Long userId, AddressRequest request) {
        int isDefault = Boolean.TRUE.equals(request.isDefault() != null && request.isDefault() == 1) ? 1 : 0;
        if (isDefault == 1) addressMapper.clearDefault(userId);
        addressMapper.insert(userId, request.receiver(), request.phone(), request.detail(), isDefault);
    }

    @Transactional
    public void updateAddress(Long userId, Long id, AddressRequest request) {
        if (addressMapper.findById(userId, id) == null) throw new IllegalArgumentException("地址不存在");
        int isDefault = request.isDefault() != null && request.isDefault() == 1 ? 1 : 0;
        if (isDefault == 1) addressMapper.clearDefault(userId);
        addressMapper.update(userId, id, request.receiver(), request.phone(), request.detail(), isDefault);
    }

    @Transactional
    public void deleteAddress(Long userId, Long id) {
        if (addressMapper.delete(userId, id) == 0) throw new IllegalArgumentException("地址不存在");
    }
}
