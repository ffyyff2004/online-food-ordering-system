package com.foodordering.mapper;

import com.foodordering.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    int insert(User user);
    User findById(Long id);
    int updateProfile(@org.apache.ibatis.annotations.Param("id") Long id,
                      @org.apache.ibatis.annotations.Param("nickname") String nickname,
                      @org.apache.ibatis.annotations.Param("phone") String phone,
                      @org.apache.ibatis.annotations.Param("avatar") String avatar);
}
