package com.petal.gateway.filter;

import com.petal.common.base.enums.ResponseType;
import com.petal.common.base.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 全局过滤器。记录所有发送到网关的请求的各种数据
 *
 * @author youzhengjie
 * @date 2023/05/15 16:18:06
 */
@Slf4j
@Component // 将全局过滤器放到Spring容器中,使之全局生效
public class RequestLoggingFilter implements GlobalFilter, Ordered {

	/**
	 * 统计开始时间的key
	 */
	private static final String START_TIME_KEY = "startTime";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// >>>>>请求进入过滤器之前（pre filter）执行 （可用于统计请求的开始执行时间）
		String info = String.format("执行的方法名: %s ,主机名: %s ,路径: %s ,查询参数: %s ",
				exchange.getRequest().getMethod().name(), exchange.getRequest().getURI().getHost(),
				exchange.getRequest().getURI().getPath(), exchange.getRequest().getQueryParams());
		log.info(info);

		// 统计请求开始时间（这个时间就是当请求进入过滤器之前统计）,并将刚刚统计的请求开始时间放到ServerWebExchange中
		exchange.getAttributes().put(START_TIME_KEY,System.currentTimeMillis());

		// >>>>>下面的chain.filter(exchange)意思是放行该请求,让该请求可以访问目标接口
		// >>>>>>特别注意: 下面的then(xxx)意思是当该请求被放行后、并且访问目标接口结束后才执行。相当于目标接口执行完之后的回调方法（可用于统计请求的结束时间、接口耗时）
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			// >>>>>这里的代码只会当该请求被放行后、并且访问目标接口结束后才执行（目标接口执行完之后的回调方法）

			// 拿到我们刚刚放到ServerWebExchange中的请求开始时间
			Long startTime = exchange.getAttribute(START_TIME_KEY);
			if (startTime != null) {
				// 请求结束时间
				long endTime = System.currentTimeMillis();
				// 请求耗时
				Long executeTime = (endTime - startTime);
				// 获取用户的ip地址
				String ipAddr = IpUtil.getIpAddrByServerHttpRequest(exchange.getRequest());
				// 获取目标接口路径
				String path = exchange.getRequest().getURI().getRawPath();

				// 响应失败编码
				int code = ResponseType.ERROR.getCode();

				//当exchange.getResponse().getStatusCode()不为空则拿这个的响应码,否则则拿上面定义的ResponseType.ERROR.getCode()
				if (exchange.getResponse().getStatusCode() != null) {
					code = exchange.getResponse().getStatusCode().value();
				}

				log.info("来自IP地址: {} 的用户,访问目标接口: {} ,响应状态码: {} ,请求耗时: {}ms",
						ipAddr, path, code, executeTime);
			}
		}));
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
