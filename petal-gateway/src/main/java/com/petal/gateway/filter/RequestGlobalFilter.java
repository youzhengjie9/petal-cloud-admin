package com.petal.gateway.filter;

import com.petal.common.base.constant.Oauth2Constant;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;


/**
 * 请求全局过滤器
 * <p>
 * 作用:
 * 1. 对请求头中名为only_feign_call的参数进行去除（具体为什么可以看@PermitAll注解）
 * 2. 代码实现StripPrefix过滤器
 *
 * @author youzhengjie
 * @date 2023/05/24 09:55:46
 */
@Component // 将全局过滤器放到Spring容器中,使之全局生效
public class RequestGlobalFilter implements GlobalFilter, Ordered {


	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// 把从网关进来的请求的请求头（only_feign_call）去除。
		// 因为我们要控制@PermitAll注解的onlyFeignCall属性设置为true的接口只能通过openfeign所访问。
		// 如果在浏览器通过访问gateway的方式访问该接口则会访问失败.（原理就是在网关把请求的请求头（only_feign_call）去除即可）
		ServerHttpRequest request = exchange.getRequest().mutate().headers(httpHeaders -> {
			httpHeaders.remove(Oauth2Constant.ONLY_FEIGN_CALL_HEADER_NAME);
			// 记录当前请求的开始时间
			httpHeaders.put(Oauth2Constant.REQUEST_START_TIME,
					Collections.singletonList(String.valueOf(System.currentTimeMillis())));
		}).build();

		// >>>>> 代码实现StripPrefix = 1同样的功能 (这样我们就不需要在application.yml配置filters为StripPrefix = 1了)
		// （例如访问http://网关ip:网关端口/petal-auth/bbb,实际上访问的是http://网关ip:网关端口/bbb ）
		addOriginalRequestUrl(exchange, request.getURI());
		String rawPath = request.getURI().getRawPath();
		String newPath = "/" + Arrays.stream(StringUtils.tokenizeToStringArray(rawPath, "/"))
				// StripPrefix = 1
			.skip(1L)
			.collect(Collectors.joining("/"));

		ServerHttpRequest newRequest = request.mutate().path(newPath).build();
		exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newRequest.getURI());

		return chain.filter(exchange.mutate().request(newRequest.mutate().build()).build());
	}

	/**
	 * 优先级数字越低,越先执行
	 *
	 * @return int
	 */
	@Override
	public int getOrder() {
		return 10;
	}

}
