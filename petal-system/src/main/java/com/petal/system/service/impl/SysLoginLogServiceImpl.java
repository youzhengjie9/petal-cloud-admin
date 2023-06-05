package com.petal.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petal.common.base.entity.SysLoginLog;
import com.petal.system.mapper.SysLoginLogMapper;
import com.petal.system.service.SysLoginLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 登录日志服务impl
 *
 * @author youzhengjie
 * @date 2022/10/21 23:29:47
 */
@Service
@Slf4j
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

    private SysLoginLogMapper sysLoginLogMapper;

    @Autowired
    public void setSysLoginLogMapper(SysLoginLogMapper sysLoginLogMapper) {
        this.sysLoginLogMapper = sysLoginLogMapper;
    }

    @Override
    public List<SysLoginLog> selectAllLoginLogByLimit(int page, int size) {

        return sysLoginLogMapper.selectAllLoginLogByLimit(page, size);
    }

    @Override
    public List<SysLoginLog> searchLoginLogByUserNameAndLimit(String username, int page, int size) {

        return sysLoginLogMapper.searchLoginLogByUserNameAndLimit(username, page, size);
    }

    @Override
    public Integer searchLoginLogCountByUserName(String username) {
        return sysLoginLogMapper.searchLoginLogCountByUserName(username);
    }

}
