package com.petal.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petal.common.base.enums.ResponseType;
import com.petal.common.base.utils.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 全局异常处理。
 * <p>
 * 网关异常通用处理器，只作用在webflux环境下
 *
 * @author youzhengjie
 * @date 2023/05/18 16:38:52
 */
@Slf4j
@Order(-1)
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		ServerHttpResponse response = exchange.getResponse();

		if (response.isCommitted()) {
			return Mono.error(ex);
		}

		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		if (ex instanceof ResponseStatusException) {
			response.setStatusCode(((ResponseStatusException) ex).getStatus());
		}

		return response.writeWith(Mono.fromSupplier(() -> {
			DataBufferFactory bufferFactory = response.bufferFactory();
			try {
				ResponseResult<Object> responseResult = ResponseResult.build(ResponseType.ERROR.getCode(), ex.getMessage(), null);
				log.warn("网关异常: {} {}", exchange.getRequest().getPath(), ex.getMessage());
				return bufferFactory.wrap(objectMapper.writeValueAsBytes(responseResult));
			}
			catch (JsonProcessingException e) {
				log.error("输出响应异常", ex);
				return bufferFactory.wrap(new byte[0]);
			}
		}));
	}

}
