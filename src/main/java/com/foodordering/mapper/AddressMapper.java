package com.foodordering.mapper;

import com.foodordering.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {
    List<UserAddress> findByUserId(@Param("userId") Long userId);
    UserAddress findById(@Param("userId") Long userId, @Param("id") Long id);
    int clearDefault(@Param("userId") Long userId);
    int insert(@Param("userId") Long userId, @Param("receiver") String receiver,
               @Param("phone") String phone, @Param("detail") String detail,
               @Param("isDefault") int isDefault);
    int update(@Param("userId") Long userId, @Param("id") Long id,
               @Param("receiver") String receiver, @Param("phone") String phone,
               @Param("detail") String detail, @Param("isDefault") int isDefault);
    int delete(@Param("userId") Long userId, @Param("id") Long id);
}
