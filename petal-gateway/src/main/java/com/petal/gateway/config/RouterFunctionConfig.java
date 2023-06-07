package com.petal.gateway.config;

import com.petal.gateway.handler.ImageCaptchaHandler;
import com.petal.gateway.handler.SendSmsCaptchaHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


/**
 * 路由方法配置
 *
 * @author youzhengjie
 * @date 2023/05/15 16:05:18
 */
@Slf4j
@Configuration
public class RouterFunctionConfig {

	private ImageCaptchaHandler imageCaptchaHandler;

	private SendSmsCaptchaHandler sendSmsCaptchaHandler;

	@Autowired
	public void setImageCaptchaHandler(ImageCaptchaHandler imageCaptchaHandler) {
		this.imageCaptchaHandler = imageCaptchaHandler;
	}

	@Autowired
	public void setSendSmsCaptchaHandler(SendSmsCaptchaHandler sendSmsCaptchaHandler) {
		this.sendSmsCaptchaHandler = sendSmsCaptchaHandler;
	}

	/**
	 * 生成图片验证码的接口
	 * <p>
	 * 下面这个方法相当于 @GetMapping(path="/captcha") 注解
	 *
	 * @return {@link RouterFunction}<{@link ServerResponse}>
	 */
	@Bean
	public RouterFunction<ServerResponse> imageCaptchaRouterFunction() {
		return RouterFunctions.route(
				RequestPredicates.GET("/image/captcha"),
				imageCaptchaHandler);
	}

	@Bean
	public RouterFunction<ServerResponse> sendCaptchaRouterFunction(){
		return RouterFunctions.route(
				RequestPredicates.GET("/send/sms/captcha/{phone}"),
				sendSmsCaptchaHandler
		);
	}

}
