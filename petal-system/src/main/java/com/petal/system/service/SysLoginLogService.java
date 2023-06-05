package com.petal.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.petal.common.base.entity.SysLoginLog;

import java.util.List;

/**
 * 登录日志服务
 *
 * @author youzhengjie
 * @date 2022/10/21 23:26:16
 */
public interface SysLoginLogService extends IService<SysLoginLog> {

    List<SysLoginLog> selectAllLoginLogByLimit(int page, int size);

    List<SysLoginLog> searchLoginLogByUserNameAndLimit(String username,
                                                       int page,
                                                       int size);

    Integer searchLoginLogCountByUserName(String username);
}
