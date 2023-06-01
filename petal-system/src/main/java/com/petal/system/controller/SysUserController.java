package com.petal.system.controller;

import com.petal.common.base.entity.SysUser;
import com.petal.common.base.utils.ResponseResult;
import com.petal.common.security.annotation.PermitAll;
import com.petal.common.security.utils.SecurityUtil;
import com.petal.system.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器
 *
 * @author youzhengjie
 * @date 2023/05/17 00:00:24
 */
@Api("用户控制器")
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    private SysUserService sysUserService;

    @Autowired
    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /**
     * 获取当前用户的信息
     *
     * @return {@link String}
     */
    @GetMapping(path = "/getUserInfo")
    public ResponseResult<SysUser> getUserInfo(){
        // 通过请求头Authorization获取accessToken,从而获取到当前用户的信息
        String username = SecurityUtil.getUsername();
        SysUser sysUser = sysUserService.lambdaQuery().eq(SysUser::getUserName, username).one();
        return ResponseResult.ok(sysUser);
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return {@link ResponseResult}<{@link SysUser}>
     */
    @PermitAll
    @GetMapping(path = "/queryUserByUserName/{username}")
    @ApiOperation("根据用户名查询用户信息")
    public ResponseResult<SysUser> queryUserByUserName(@PathVariable("username") String username){

        return ResponseResult.ok(sysUserService.lambdaQuery().eq(SysUser::getUserName, username).one());
    }

    /**
     * 根据手机号查询用户信息
     *
     * @param phone 电话
     * @return {@link ResponseResult}<{@link SysUser}>
     */
    @PermitAll
    @GetMapping(path = "/queryUserByPhone/{phone}")
    @ApiOperation("根据手机号查询用户信息")
    public ResponseResult<SysUser> queryUserByPhone(@PathVariable("phone") String phone){

        return ResponseResult.ok(sysUserService.lambdaQuery().eq(SysUser::getPhone, phone).one());
    }

}
