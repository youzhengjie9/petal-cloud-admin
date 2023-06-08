package com.petal.system.controller;

import com.petal.common.base.annotation.OperLog;
import com.petal.common.base.enums.ResponseType;
import com.petal.common.base.utils.ResponseResult;
import com.petal.common.base.vo.ServerInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器监控控制器
 *
 * @author youzhengjie
 * @date 2022/10/21 15:50:17
 */
@RestController
@Api("服务器监控控制器")
@RequestMapping(path = "/sys/server/monitor")
public class SysServerMonitorController {


    /**
     * 获取该服务器所有信息
     *
     * @return {@link ResponseResult}<{@link ServerInfoVO}>
     */
    @PreAuthorize("@pms.hasPermission('sys:monitor:server')")
    @OperLog("获取该服务器所有信息")
    @GetMapping(path = "/getServerInfo")
    @ApiOperation("获取该服务器所有信息")
    public ResponseResult<ServerInfoVO> getServerInfo(){

        try {
            ServerInfoVO serverInfoVO = ServerInfoVO.init();
            return ResponseResult.build
                    (ResponseType.SUCCESS.getCode(), ResponseType.SUCCESS.getMessage(),serverInfoVO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.build
                    (ResponseType.ERROR.getCode(), ResponseType.ERROR.getMessage(),null);
        }
    }

}
