package com.petal.system.controller;

import com.petal.common.base.utils.ResponseResult;
import com.petal.common.base.dto.SysUserRegisterDTO;
import com.petal.system.service.SysRegisterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 注册用户控制器
 *
 * @author youzhengjie
 * @date 2022/10/26 00:23:31
 */
@RestController
@Api("注册用户控制器")
@RequestMapping(path = "/register")
public class SysRegisterController {

    private SysRegisterService sysRegisterService;

    @Autowired
    public void setSysRegisterService(SysRegisterService sysRegisterService) {
        this.sysRegisterService = sysRegisterService;
    }

    /**
     * 注册
     *
     * @param sysUserRegisterDTO 用户注册dto
     * @return {@link ResponseResult}
     */
    @PostMapping(path = "/")
    @ApiOperation("注册用户接口")
    public ResponseResult register(@RequestBody @Valid SysUserRegisterDTO sysUserRegisterDTO){

        return sysRegisterService.register(sysUserRegisterDTO);
    }

    /**
     * 发送代码
     *
     * @param phone 电话
     * @return {@link ResponseResult}<{@link String}>
     */
    @GetMapping(path = "/sendCode")
    @ApiOperation("发送手机验证码")
    public ResponseResult<String> sendCode(@RequestParam("phone") String phone){

        return sysRegisterService.sendCode(phone);
    }


}
