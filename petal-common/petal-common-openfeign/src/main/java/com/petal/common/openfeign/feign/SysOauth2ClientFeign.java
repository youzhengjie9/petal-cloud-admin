package com.petal.common.openfeign.feign;

import com.petal.common.base.constant.Oauth2Constant;
import com.petal.common.base.utils.ResponseResult;
import com.petal.common.openfeign.constant.ApplicationNameConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = ApplicationNameConstant.PETAL_SYSTEM_SERVER,path = "/sys/oauth2/client")
public interface SysOauth2ClientFeign {

    @GetMapping(value = "/getClientById/{clientId}")
    public ResponseResult getClientById(@PathVariable("clientId") String clientId,
                                        @RequestHeader(Oauth2Constant.ONLY_FEIGN_CALL_HEADER_NAME)
                                        String onlyFeignCallHeader);



}
