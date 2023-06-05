package com.petal.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petal.common.base.entity.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 操作日志映射器
 *
 * @author youzhengjie
 * @date 2022/10/21 23:30:48
 */
@Mapper
@Repository
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {

    List<SysOperationLog> selectAllOperationLogByLimit(@Param("page") int page, @Param("size") int size);


}
