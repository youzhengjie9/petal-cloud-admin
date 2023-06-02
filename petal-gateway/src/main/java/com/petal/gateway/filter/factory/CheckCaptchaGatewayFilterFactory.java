package com.petal.gateway.filter.factory;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petal.common.base.constant.Oauth2Constant;
import com.petal.common.base.constant.RedisConstant;
import com.petal.common.base.enums.ResponseType;
import com.petal.common.base.utils.ResponseResult;
import com.petal.common.base.utils.WebUtil;
import com.petal.gateway.exception.CheckCaptchaException;
import com.petal.gateway.properties.GatewayConfigProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


/**
 * 检查验证码过滤过滤器
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
 * @date 2023/05/17 09:56:52
 */
@Slf4j
@Component
public class CheckCaptchaGatewayFilterFactory extends AbstractGatewayFilterFactory  {

	private GatewayConfigProperties gatewayConfigProperties;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private RedisTemplate redisTemplate;

	@Autowired
	public void setGatewayConfigProperties(GatewayConfigProperties gatewayConfigProperties) {
		this.gatewayConfigProperties = gatewayConfigProperties;
	}

	@Autowired
	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public GatewayFilter apply(Object config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			//判断是否是登录请求（/oauth2/token）
			boolean isLoginRequest = CharSequenceUtil.containsAnyIgnoreCase(request.getURI().getPath(),
					Oauth2Constant.OAUTH_TOKEN_URL);

			// 如果不是登录请求,则直接放行
			if (!isLoginRequest) {
				return chain.filter(exchange);
			}

			// 如果是刷新token请求,则直接放行
			String grantType = request.getQueryParams().getFirst("grant_type");
			if (StrUtil.equals(Oauth2Constant.REFRESH_TOKEN_GRANT_TYPE, grantType)) {
				return chain.filter(exchange);
			}
			// 判断该客户端是否不用检查验证码
			boolean isIgnoreCheckCaptchaClient = gatewayConfigProperties.getIgnoreCheckCaptchaClients().contains(WebUtil.getClientId(request));

			try {
				// 如果该客户端需要检查验证码（帐号密码登录的验证码、手机号登录的验证码）
				if (!isIgnoreCheckCaptchaClient) {
					checkCode(request);
				}
			}
			catch (Exception e) {
				ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.PRECONDITION_REQUIRED);
				response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
				//抛出的异常信息（比如checkCode方法抛出的异常的信息这里就能获取到）
				final String errMsg = e.getMessage();
				return response.writeWith(Mono.create(monoSink -> {
					try {
						//将抛出的异常转成json并返回给用户
						ResponseResult<Object> responseResult =
								ResponseResult.build(ResponseType.ERROR.getCode(), errMsg, null);
						byte[] bytes = objectMapper.writeValueAsBytes(responseResult);
						DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
						monoSink.success(dataBuffer);
					}
					catch (JsonProcessingException jsonProcessingException) {
						log.error("对象输出异常", jsonProcessingException);
						monoSink.error(jsonProcessingException);
					}
				}));
			}

			return chain.filter(exchange);
		};
	}

	/**
	 * 检查验证码（检查帐号密码登录的验证码、手机号登录的验证码）
	 *
	 * @param request 请求
	 */
	@SneakyThrows
	private void checkCode(ServerHttpRequest request) {

		String phone = request.getQueryParams().getFirst(Oauth2Constant.PHONE_PARAMETER_NAME);

		// 如果前端传来的手机号(phone)为空,则《检查帐号密码登录的验证码》
		if(StringUtils.isBlank(phone)){
			//拿到图片验证码的key（通过这个key可以去redis中获取正确的验证码）
			String imageCaptchaKey = request.getQueryParams().getFirst(Oauth2Constant.IMAGE_CAPTCHA_KEY_PARAMETER_NAME);
			//从请求中拿到图片验证码（帐号密码登录的验证码）
			String imageCaptcha = request.getQueryParams().getFirst(Oauth2Constant.IMAGE_CAPTCHA_PARAMETER_NAME);
			//如果imageCaptcha为空
			if (StringUtils.isBlank(imageCaptcha)) {
				throw new CheckCaptchaException("图片验证码不能为空");
			}
			// 正确的图片验证码(这里不能之前强制转换成String,因为前端传来的key可能是不正确的、伪造的,这是redis就没有数据,直接转成String会报错)
			Object realImageCaptcha = redisTemplate.opsForValue().get(imageCaptchaKey);

			// 如果从redis查询的验证码为空(说明前端传来验证码的key是假的)
			if (ObjectUtil.isEmpty(realImageCaptcha)) {
				throw new CheckCaptchaException("图片验证码的key不存在");
			}else {
				// 如果从redis查询的验证码不为空,并且前端传来的验证码和redis中存的正确的验证码不一致
				if(!imageCaptcha.equals(realImageCaptcha)){
					throw new CheckCaptchaException("图片验证码错误");
				}
			}
			// 当验证码匹配正确后就从redis中删除这个验证码key
			redisTemplate.delete(imageCaptchaKey);
		}
		// 如果前端传来的手机号(phone)不为空,则《检查手机号登录的验证码》
		else{
			//拿到手机验证码的key（通过这个key可以去redis中获取正确的验证码）
			String smsCaptchaKey = RedisConstant.SMS_CAPTCHA_PREFIX + phone;
			//从请求中拿到手机短信验证码
			String smsCaptcha = request.getQueryParams().getFirst(Oauth2Constant.SMS_CAPTCHA_PARAMETER_NAME);
			//如果smsCaptcha为空
			if (StringUtils.isBlank(smsCaptcha)) {
				throw new CheckCaptchaException("手机短信验证码不能为空");
			}

			// 正确的手机短信验证码(这里不能之前强制转换成String,因为前端传来的手机号可能是不正确的、伪造的,这是redis就没有数据,直接转成String会报错)
			Object realSmsCaptcha = redisTemplate.opsForValue().get(smsCaptchaKey);

			// 如果从redis查询的验证码为空(说明前端传来的手机号并没有发送过验证码)
			if (ObjectUtil.isEmpty(realSmsCaptcha)) {
				throw new CheckCaptchaException("该手机号并未发送验证码");
			}else {
				// 如果从redis查询的验证码不为空,并且前端传来的验证码和redis中存的正确的验证码不一致
				if(!smsCaptcha.equals(realSmsCaptcha)){
					throw new CheckCaptchaException("手机短信验证码错误");
				}
			}
			// 当验证码匹配正确后就从redis中删除这个验证码key
			redisTemplate.delete(smsCaptchaKey);
		}

	}

}
