package com.petal.common.security.resource.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petal.common.base.enums.ResponseType;
import com.petal.common.base.utils.ResponseResult;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 自定义认证失败处理器
 *
 * @author youzhengjie
 * @date 2023/05/12 18:16:08
 */

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	@SneakyThrows
	public void commence(HttpServletRequest request, HttpServletResponse response,
						 AuthenticationException authException) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		ResponseResult<Object> result = ResponseResult.build(ResponseType.UNAUTHORIZED);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		if (authException != null) {
			result.setMsg("认证失败");
			result.setData(authException.getMessage());
		}
		// 针对令牌过期返回特殊的code=424
		if (authException instanceof InvalidBearerTokenException
				|| authException instanceof InsufficientAuthenticationException) {
			response.setStatus(HttpStatus.FAILED_DEPENDENCY.value());
			result.setMsg("令牌过期");
		}
		PrintWriter printWriter = response.getWriter();
		printWriter.append(objectMapper.writeValueAsString(result));
	}

}
