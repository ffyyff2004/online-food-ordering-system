package com.foodordering.mapper;

import com.foodordering.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    List<Favorite> findByUserId(@Param("userId") Long userId);
    int count(@Param("userId") Long userId, @Param("foodId") Long foodId);
    int insert(@Param("userId") Long userId, @Param("foodId") Long foodId);
    int delete(@Param("userId") Long userId, @Param("foodId") Long foodId);
}
