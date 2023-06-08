package com.petal.system.controller;

import com.petal.common.base.annotation.OperLog;
import com.petal.system.service.SysExportExcelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 导出excel控制器
 *
 * @author youzhengjie
 * @date 2022/10/22 10:53:35
 */
@RestController
@Api("导出Excel控制器")
@RequestMapping(path = "/sys/export/excel")
public class SysExportExcelController {

    private SysExportExcelService sysExportExcelService;

    @Autowired
    public void setSysExportExcelService(SysExportExcelService sysExportExcelService) {
        this.sysExportExcelService = sysExportExcelService;
    }

    @OperLog("导出所有用户")
    @GetMapping("/exportAllUser")
    @ApiOperation(value = "导出所有用户")
    public void exportAllUser(HttpServletResponse response){

        sysExportExcelService.exportAllUser(response);
    }


    @OperLog("导出所有角色")
    @GetMapping("/exportAllRole")
    @ApiOperation(value = "导出所有角色")
    public void exportAllRole(HttpServletResponse response){

        sysExportExcelService.exportAllRole(response);
    }

    @OperLog("导出所有菜单")
    @GetMapping("/exportAllMenu")
    @ApiOperation(value = "导出所有菜单")
    public void exportAllMenu(HttpServletResponse response){

        sysExportExcelService.exportAllMenu(response);
    }

    @OperLog("导出所有登录日志")
    @GetMapping("/exportAllLoginLog")
    @ApiOperation(value = "导出所有登录日志")
    public void exportAllLoginLog(HttpServletResponse response){

        sysExportExcelService.exportAllLoginLog(response);
    }

    @OperLog("导出所有操作日志")
    @GetMapping("/exportAllOperationLog")
    @ApiOperation(value = "导出所有操作日志")
    public void exportAllOperationLog(HttpServletResponse response){

        sysExportExcelService.exportAllOperationLog(response);
    }


}
