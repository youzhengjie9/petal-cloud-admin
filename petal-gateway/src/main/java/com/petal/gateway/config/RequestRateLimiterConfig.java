package com.petal.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.Objects;


/**
 * 请求限流配置
 *
 * @author youzhengjie
 * @date 2023/05/14 22:47:47
 */
@Configuration(proxyBeanMethods = false)
public class RequestRateLimiterConfig {

	/**
	 * 作用: 把用户的IP作为限流的key进行限流
	 *
	 * @return {@link KeyResolver}
	 */
	@Bean
	@Primary //优先用这个bean
	public KeyResolver ipAddressKeyResolver() {
		return exchange -> Mono
			.just(Objects.requireNonNull(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()))
				.getAddress()
				.getHostAddress());
	}

	/**
	 * 接口限流
	 * 获取请求地址的uri作为限流key
	 *
	 * @return {@link KeyResolver}
	 */
	@Bean
	public KeyResolver apiKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getPath().value());
	}

}
