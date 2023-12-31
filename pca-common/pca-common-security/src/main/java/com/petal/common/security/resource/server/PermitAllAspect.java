package com.petal.common.security.resource.server;

import com.petal.common.base.constant.Oauth2Constant;
import com.petal.common.security.annotation.PermitAll;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.http.HttpServletRequest;


/**
 * 触发 @PermitAll注解的效果
 *
 * @author youzhengjie
 * @date 2023/05/12 17:45:45
 */
@Slf4j
@Aspect
public class PermitAllAspect implements Ordered {

	private final HttpServletRequest request;

	public PermitAllAspect(HttpServletRequest request) {
		this.request = request;
	}

	@SneakyThrows
	@Around("@within(permitAll) || @annotation(permitAll)")
	public Object around(ProceedingJoinPoint point, PermitAll permitAll) {
		String headerName = Oauth2Constant.ONLY_FEIGN_CALL_HEADER_NAME;
		//从请求中拿到名为only_feign_call的请求头的值
		String onlyFeignCallHeader = request.getHeader(headerName);
		// 如果该PermitAll注解的onlyFeignCall属性值设置了为true,则要进一步判断
		if (permitAll.onlyFeignCall()) {
			// 如果onlyFeignCallHeader为空则直接抛出异常（访问失败）
			if(StringUtils.isBlank(onlyFeignCallHeader)){
				String errMsg = String.format("访问接口 %s 失败,因为该PermitAll注解的onlyFeignCall属性值设置了为true,但是名为 %s 的请求头内容为空,所以禁止访问", point.getSignature().getName(), headerName);
				log.error(errMsg);
				throw new AccessDeniedException(errMsg);
			}
		}
		//如果该PermitAll注解的onlyFeignCall属性值设置了为false
		//说明该接口既可以被feign访问,也可以被外网通过网关所访问,所以不需要检查名为only_feign_call的请求头的值,直接放行
		return point.proceed();
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 1;
	}

}
