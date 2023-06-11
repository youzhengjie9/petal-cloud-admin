package com.petal.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petal.common.base.annotation.OperLog;
import com.petal.common.base.entity.SysOperationLog;
import com.petal.common.base.enums.ResponseType;
import com.petal.common.base.utils.ResponseResult;
import com.petal.system.service.SysOperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志控制器
 *
 * @author youzhengjie
 * @date 2022/10/21 23:56:35
 */
@RestController
@Api("操作日志控制器")
@RequestMapping(path = "/sys/operation/log")
public class SysOperationLogController {

    private SysOperationLogService sysOperationLogService;

    @Autowired
    public void setSysOperationLogService(SysOperationLogService sysOperationLogService) {
        this.sysOperationLogService = sysOperationLogService;
    }

    @PreAuthorize("@pms.hasPermission('sys:log:operation')")
    @OperLog("查询所有操作日志并分页")
    @GetMapping(path = "/selectAllOperationLogByLimit")
    @ApiOperation("查询所有操作日志并分页")
    public ResponseResult<List<SysOperationLog>> selectAllOperationLogByLimit(@RequestParam("page") int page,
                                                                              @RequestParam("size") int size){
        try {
            page=(page-1)*size;
            List<SysOperationLog> sysOperationLogs = sysOperationLogService.selectAllOperationLogByLimit(page, size);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(), ResponseType.SUCCESS.getMessage(),sysOperationLogs);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(), ResponseType.ERROR.getMessage(),null);
        }
    }

    @PreAuthorize("@pms.hasPermission('sys:log:operation')")
    @OperLog("查询所有操作日志数量")
    @GetMapping(path = "/selectAllOperationLogCount")
    @ApiOperation("查询所有操作日志数量")
    public ResponseResult<Long> selectAllOperationLogCount(){

        try {
            long count = sysOperationLogService.selectAllOperationLogCount();

            return ResponseResult.build
                    (ResponseType.SUCCESS.getCode(), ResponseType.SUCCESS.getMessage(),count);
        }catch (Exception e){
            return ResponseResult.build
                    (ResponseType.ERROR.getCode(), ResponseType.ERROR.getMessage(),null);
        }

    }

    @PreAuthorize("@pms.hasPermission('sys:log:operation:delete')")
    @OperLog("删除操作日志")
    @DeleteMapping(path = "/deleteOperationLog")
    @ApiOperation("删除操作日志")
    public ResponseResult<Boolean> deleteOperationLog(@RequestParam("id") long id){
        try {
            LambdaQueryWrapper<SysOperationLog> sysOperationLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sysOperationLogLambdaQueryWrapper.eq(SysOperationLog::getId,id);
            boolean removeResult = sysOperationLogService.remove(sysOperationLogLambdaQueryWrapper);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(), ResponseType.SUCCESS.getMessage(),removeResult);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(), ResponseType.ERROR.getMessage(),null);
        }
    }

    @PreAuthorize("@pms.hasPermission('sys:log:operation')")
    @OperLog("根据username分页查询操作日志")
    @GetMapping(path = "/searchOperationLogByUserNameAndLimit")
    @ApiOperation("根据username分页查询操作日志")
    public ResponseResult<List<SysOperationLog>> searchOperationLogByUserNameAndLimit(@RequestParam("username") String username,
                                                                           @RequestParam("page") int page,
                                                                           @RequestParam("size") int size){
        try {
            List<SysOperationLog> sysOperationLogList =
                    sysOperationLogService.searchOperationLogByUserNameAndLimit(username, page, size);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(), ResponseType.SUCCESS.getMessage(),sysOperationLogList);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(), ResponseType.ERROR.getMessage(),null);
        }
    }

    @PreAuthorize("@pms.hasPermission('sys:log:operation')")
    @GetMapping(path = "/searchOperationLogCountByUserName")
    @ApiOperation("根据username查询符合条件的操作日志数量")
    public ResponseResult<Long> searchOperationLogCountByUserName(@RequestParam("username") String username){

        try {
            long count = sysOperationLogService.searchOperationLogCountByUserName(username);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(),
                    ResponseType.SUCCESS.getMessage(),count);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(),
                    ResponseType.ERROR.getMessage(),null);
        }
    }

}
