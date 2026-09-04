package com.foodordering.mapper;

import com.foodordering.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OperationLogMapper {
    int insert(@Param("adminId") Long adminId, @Param("action") String action,
               @Param("method") String method, @Param("requestPath") String requestPath,
               @Param("detail") String detail);
    List<OperationLog> findAll(@Param("limit") int limit);
}
