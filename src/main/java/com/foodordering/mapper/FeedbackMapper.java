package com.foodordering.mapper;

import com.foodordering.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FeedbackMapper {
    int insert(@Param("userId") Long userId, @Param("content") String content);
    List<Feedback> findByUserId(@Param("userId") Long userId);
    List<Feedback> findAll(@Param("status") String status);
    int reply(@Param("id") Long id, @Param("reply") String reply);
}
