package com.petal.gateway.handler;

import com.alibaba.fastjson2.JSON;
import com.petal.common.base.constant.RedisConstant;
import com.petal.common.base.enums.ResponseType;
import com.petal.common.base.utils.ResponseResult;
import com.wf.captcha.SpecCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 生成图片验证码处理器
 *
 * @author youzhengjie
 * @date 2023/05/15 15:53:05
 */
@Component
public class ImageCaptchaHandler implements HandlerFunction<ServerResponse> {

    /**
     * 图片验证码宽度
     */
    private static final Integer IMAGE_CAPTCHA_WIDTH = 130;

    /**
     * 图片验证码高度
     */
    private static final Integer IMAGE_CAPTCHA_HEIGHT = 48;

    /**
     * 图片验证码长度
     */
    private static final Integer IMAGE_CAPTCHA_LENTH = 5;

    private RedisTemplate redisTemplate;

    @Autowired
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {

        // 三个参数分别为宽、高、位数
        SpecCaptcha captcha = new SpecCaptcha(IMAGE_CAPTCHA_WIDTH, IMAGE_CAPTCHA_HEIGHT, IMAGE_CAPTCHA_LENTH);
        // 获取我们刚刚生成的验证码
        String code = captcha.text().toLowerCase();
        // 验证码图片的base64编码
        String imageCaptchaBase64 = captcha.toBase64();
        // 随机生成一个验证码id作为key，返回给前端，前端可以通过这个验证码key在redis中找到正确的验证码
        String imageCaptchaKey = RedisConstant.IMAGE_CAPTCHA_PREFIX+UUID.randomUUID().toString().replaceAll("-","");
        // 将验证码存入Redis中,并设置有效期30分钟
        redisTemplate.opsForValue().set(imageCaptchaKey,code,30, TimeUnit.MINUTES);
        Map<Object, Object> map = new ConcurrentHashMap<>();
        map.put("imageCaptchaKey",imageCaptchaKey);
        map.put("imageCaptchaBase64",imageCaptchaBase64);

        ResponseResult<Map<Object, Object>> responseResult =
                ResponseResult.build(ResponseType.SUCCESS, map);
        String json = JSON.toJSONString(responseResult);

        return ServerResponse.status(HttpStatus.OK)
                .bodyValue(json);
    }

}
