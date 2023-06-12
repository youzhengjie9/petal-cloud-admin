package com.petal.common.openfeign.feign;

import com.petal.common.base.constant.Oauth2Constant;
import com.petal.common.base.entity.SysLoginLog;
import com.petal.common.base.utils.ResponseResult;
import com.petal.common.openfeign.constant.ApplicationNameConstant;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@FeignClient(value = ApplicationNameConstant.PETAL_SYSTEM_SERVER,path = "/sys/login/log")
public interface SysLoginLogFeign {

    @PostMapping(path = "/addLoginLog")
    @ApiOperation("添加登录日志")
    public ResponseResult<Boolean> addLoginLog(@RequestBody SysLoginLog sysLoginLog,
                                               @RequestHeader(Oauth2Constant.ONLY_FEIGN_CALL_HEADER_NAME)
                                                   String onlyFeignCallHeader);


}
