package com.petal.gateway.swagger;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;


/**
 * swagger网关过滤器工厂
 * <p>
 * 自定义filters格式:
 * 1: 继承AbstractGatewayFilterFactory，
 * 2: 类名必须以GatewayFilterFactory为结尾
 * <p>
 * 例如自定义一个CheckCaptcha的过滤器:
 * 1: 创建一个名为CheckCaptchaGatewayFilterFactory的类,并继承AbstractGatewayFilterFactory
 * 2: 在application.yml中的gateway.filters下面配置 CheckCaptcha 即可。
 *
 * @author youzhengjie
 * @date 2023/05/16 22:00:23
 */
@Component
public class SwaggerGatewayFilterFactory extends AbstractGatewayFilterFactory {
    private static final String HEADER_NAME = "X-Forwarded-Prefix";
 
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            if (!StringUtils.endsWithIgnoreCase(path, SwaggerProvider.API_URI)) {
                return chain.filter(exchange);
            }
            String basePath = path.substring(0, path.lastIndexOf(SwaggerProvider.API_URI));
            ServerHttpRequest newRequest = request.mutate().header(HEADER_NAME, basePath).build();
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
            return chain.filter(newExchange);
        };
    }
}