package com.petal.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petal.common.base.annotation.OperLog;
import com.petal.common.base.entity.SysLoginLog;
import com.petal.common.base.enums.ResponseType;
import com.petal.common.base.utils.*;
import com.petal.common.security.annotation.PermitAll;
import com.petal.system.service.SysLoginLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志控制器
 *
 * @author youzhengjie
 * @date 2022/10/21 23:19:59
 */
@RestController
@Api("登录日志控制器")
@RequestMapping(path = "/sys/login/log")
public class SysLoginLogController {

    private SysLoginLogService sysLoginLogService;

    @Autowired
    public void setSysLoginLogService(SysLoginLogService sysLoginLogService) {
        this.sysLoginLogService = sysLoginLogService;
    }

    /**
     * 查询所有登录日志并分页
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@pms.hasPermission('sys:log:login')")
    @OperLog("查询所有登录日志并分页")
    @GetMapping(path = "/selectAllLoginLogByLimit")
    @ApiOperation("查询所有登录日志并分页")
    public ResponseResult<List<SysLoginLog>> selectAllLoginLogByLimit(@RequestParam("page") int page,
                                                                      @RequestParam("size") int size){
        try {
            //分页
            page=(page-1)*size;
            List<SysLoginLog> sysLoginLogList = sysLoginLogService.selectAllLoginLogByLimit(page, size);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(), ResponseType.SUCCESS.getMessage(),sysLoginLogList);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(), ResponseType.ERROR.getMessage(),null);
        }
    }

    @PreAuthorize("@pms.hasPermission('sys:log:login')")
    @OperLog("查询所有登录日志数量")
    @GetMapping(path = "/selectAllLoginLogCount")
    @ApiOperation("查询所有登录日志数量")
    public ResponseResult<Long> selectAllLoginLogCount(){

        try {
            long count = sysLoginLogService.count();
            return ResponseResult.build
                    (ResponseType.SUCCESS.getCode(), ResponseType.SUCCESS.getMessage(),count);
        }catch (Exception e){
            return ResponseResult.build
                    (ResponseType.ERROR.getCode(), ResponseType.ERROR.getMessage(),null);
        }
    }

    @PreAuthorize("@pms.hasPermission('sys:log:login:delete')")
    @OperLog("删除登录日志")
    @DeleteMapping(path = "/deleteLoginLog")
    @ApiOperation("删除登录日志")
    public ResponseResult<Boolean> deleteLoginLog(@RequestParam("id") long id){
        try {
            LambdaQueryWrapper<SysLoginLog> loginLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
            loginLogLambdaQueryWrapper.eq(SysLoginLog::getId,id);
            boolean removeResult = sysLoginLogService.remove(loginLogLambdaQueryWrapper);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(), ResponseType.SUCCESS.getMessage(),removeResult);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(), ResponseType.ERROR.getMessage(),null);
        }
    }

    /**
     * 根据用户名分页搜索登录日志
     *
     * @param username 用户名
     * @param page     页面
     * @param size     大小
     * @return {@link List}<{@link SysLoginLog}>
     */
    @PreAuthorize("@pms.hasPermission('sys:log:login')")
    @OperLog("根据用户名分页搜索登录日志")
    @GetMapping(path = "/searchLoginLogByUserNameAndLimit")
    @ApiOperation("根据用户名分页搜索登录日志")
    public ResponseResult<List<SysLoginLog>> searchLoginLogByUserNameAndLimit(@RequestParam("username") String username,
                                                           @RequestParam("page") int page,
                                                           @RequestParam("size") int size){
        try {
            List<SysLoginLog> loginLogList = sysLoginLogService.searchLoginLogByUserNameAndLimit(username, page, size);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(), ResponseType.SUCCESS.getMessage(),loginLogList);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(), ResponseType.ERROR.getMessage(),null);
        }
    }

    /**
     * 根据用户名查询符合条件的数量
     *
     * @param username 用户名
     * @return {@link ResponseResult}
     */
    @PreAuthorize("@pms.hasPermission('sys:log:login')")
    @GetMapping(path = "/searchLoginLogCountByUserName")
    @ApiOperation("根据用户名查询符合条件的数量")
    public ResponseResult<Integer> searchLoginLogCountByUserName(@RequestParam("username") String username){

        try {
            int count = sysLoginLogService.searchLoginLogCountByUserName(username);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(),
                    ResponseType.SUCCESS.getMessage(),count);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(),
                    ResponseType.ERROR.getMessage(),null);
        }
    }

    @PermitAll
    @PostMapping(path = "/addLoginLog")
    @ApiOperation("添加登录日志")
    public ResponseResult<Boolean> addLoginLog(@RequestBody SysLoginLog sysLoginLog){
        try {
            boolean saveSuccess = sysLoginLogService.save(sysLoginLog);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(),
                    ResponseType.SUCCESS.getMessage(),saveSuccess);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(),
                    ResponseType.ERROR.getMessage(),null);
        }
    }

}
