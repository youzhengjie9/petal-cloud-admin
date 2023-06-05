package com.petal.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petal.common.base.entity.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 登录日志映射器
 *
 * @author youzhengjie
 * @date 2022/10/21 23:24:36
 */
@Mapper
@Repository
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {

    List<SysLoginLog> selectAllLoginLogByLimit(@Param("page") int page, @Param("size") int size);

    List<SysLoginLog> searchLoginLogByUserNameAndLimit(@Param("username") String username,
                                                       @Param("page") int page,
                                                       @Param("size") int size);

    Integer searchLoginLogCountByUserName(@Param("username") String username);

}
