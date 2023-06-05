package com.petal.system.service;

import com.petal.common.base.utils.ResponseResult;
import com.petal.common.base.dto.SysUserRegisterDTO;

/**
 * 注册服务
 *
 * @author youzhengjie
 * @date 2022/10/25 23:45:02
 */
public interface SysRegisterService {

    /**
     * 注册
     *
     * @param sysUserRegisterDTO 用户注册dto
     * @return {@link ResponseResult}
     */
    ResponseResult register(SysUserRegisterDTO sysUserRegisterDTO);


    ResponseResult<String> sendCode(String phone);
}
