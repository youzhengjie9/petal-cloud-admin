package com.petal.gateway.handler;

import cn.hutool.core.util.PhoneUtil;
import com.alibaba.fastjson2.JSON;
import com.petal.common.base.constant.RedisConstant;
import com.petal.common.base.enums.ResponseType;
import com.petal.common.base.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
public class SendSmsCaptchaHandler implements HandlerFunction<ServerResponse> {

    private RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 生成手机6位数字验证码
     *
     */
    private String generateCode(){
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            //在高并发下ThreadLocalRandom的性能远远大于Random的性能
            int number = ThreadLocalRandom.current().nextInt(10);
            stringBuffer.append(number);
        }
        return stringBuffer.toString();
    }

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        //接收pathVariable参数
        String phone = request.pathVariable("phone");
        ResponseResult<String> responseResult = new ResponseResult<>();

        try {
            boolean isPhone = PhoneUtil.isMobile(phone);
            //校验手机号格式是否正确
            if(isPhone){
                //生成验证码
                String code = generateCode();
                //将验证码存入redis中
                redisTemplate.opsForValue().set(RedisConstant.SMS_CAPTCHA_PREFIX + phone,code,5L, TimeUnit.MINUTES);
                responseResult.setCode(ResponseType.SEND_PHONE_CODE_SUCCESS.getCode());
                responseResult.setMsg(ResponseType.SEND_PHONE_CODE_SUCCESS.getMessage());
                String json = JSON.toJSONString(responseResult);
                return ServerResponse.status(HttpStatus.OK)
                        .bodyValue(json);
            }else {
                responseResult.setCode(ResponseType.SEND_PHONE_CODE_ERROR.getCode());
                responseResult.setMsg(ResponseType.SEND_PHONE_CODE_ERROR.getMessage());
                String json = JSON.toJSONString(responseResult);
                return ServerResponse.status(HttpStatus.OK)
                        .bodyValue(json);
            }
        }catch (Exception e){
            e.printStackTrace();
            responseResult.setCode(ResponseType.SEND_PHONE_CODE_ERROR.getCode());
            responseResult.setMsg(ResponseType.SEND_PHONE_CODE_ERROR.getMessage());
            String json = JSON.toJSONString(responseResult);
            return ServerResponse.status(HttpStatus.OK)
                    .bodyValue(json);
        }
    }
}
