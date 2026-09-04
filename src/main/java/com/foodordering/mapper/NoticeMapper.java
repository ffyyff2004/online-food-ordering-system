package com.foodordering.mapper;

import com.foodordering.dto.NoticeRequest;
import com.foodordering.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {
    List<Notice> findEnabled();
    List<Notice> findAll();
    Notice findById(@Param("id") Long id);
    int insert(@Param("request") NoticeRequest request);
    int update(@Param("id") Long id, @Param("request") NoticeRequest request);
    int updateStatus(@Param("id") Long id, @Param("status") int status);
}
