package com.petal.gateway.filter.factory;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.petal.common.base.constant.AESConstant;
import com.petal.common.base.constant.Oauth2Constant;
import com.petal.common.base.utils.AESUtil;
import com.petal.gateway.properties.GatewayConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


/**
 * 密码解密过滤器
 * <p>
 * 前端发送过来的登录表单请求的密码是要加密后才会传过来的,所以这里用来将前端加密过的密码进行解密
 *
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
 * @date 2023/05/17 09:44:33
 */
@Slf4j
@Component
public class PasswordDecoderGatewayFilterFactory extends AbstractGatewayFilterFactory {

	private static final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

	private static final String PASSWORD = "password";

	private static final String KEY_ALGORITHM = "AES";

	@Override
	public GatewayFilter apply(Object config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			// 1. 不是登录请求，直接向下执行
			if (!StrUtil.containsAnyIgnoreCase(request.getURI().getPath(), Oauth2Constant.OAUTH_TOKEN_URL)) {
				return chain.filter(exchange);
			}

			// 2. 刷新token类型，直接向下执行
			String grantType = request.getQueryParams().getFirst("grant_type");
			if (StrUtil.equals(Oauth2Constant.REFRESH_TOKEN_GRANT_TYPE, grantType)) {
				return chain.filter(exchange);
			}

			// 3. 前端加密密文解密逻辑
			Class inClass = String.class;
			Class outClass = String.class;
			ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);

			// 4. 解密生成新的报文
			Mono<?> modifiedBody = serverRequest.bodyToMono(inClass).flatMap(decryptAES());

			BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, outClass);
			HttpHeaders headers = new HttpHeaders();
			headers.putAll(exchange.getRequest().getHeaders());
			headers.remove(HttpHeaders.CONTENT_LENGTH);

			headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
			CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
			return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
				ServerHttpRequest decorator = decorate(exchange, headers, outputMessage);
				return chain.filter(exchange.mutate().request(decorator).build());
			}));
		};
	}

	/**
	 * 原文解密
	 * @return
	 */
	private Function decryptAES() {
		return s -> {
			// 获取请求密码并解密
			Map<String, String> inParamsMap = HttpUtil.decodeParamMap((String) s, CharsetUtil.CHARSET_UTF_8);
			if (inParamsMap.containsKey(PASSWORD)) {
				// 加密过的密码
				String encryptedPassword = inParamsMap.get(PASSWORD);
				log.info("用户的登录密码: {} ",encryptedPassword);
				try {
					// 将密码进行解密
					String decryptedPassword = AESUtil.decrypt(encryptedPassword, AESConstant.LOGIN_PASSWORD_AES_KEY);
					// 将修改后的密码放到一个map中
					inParamsMap.put(PASSWORD, decryptedPassword);
					log.info("解密之后的用户的登录密码: {}",decryptedPassword);
				}catch (Exception exception){
					throw new RuntimeException("用户登录的密码在网关解密失败");
				}
			}
			else {
				log.error("非法请求数据:{}", s);
			}
			return Mono.just(HttpUtil.toParams(inParamsMap, Charset.defaultCharset(), true));
		};
	}

	/**
	 * 报文转换
	 * @return
	 */
	private ServerHttpRequestDecorator decorate(ServerWebExchange exchange, HttpHeaders headers,
												CachedBodyOutputMessage outputMessage) {
		return new ServerHttpRequestDecorator(exchange.getRequest()) {
			@Override
			public HttpHeaders getHeaders() {
				long contentLength = headers.getContentLength();
				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.putAll(super.getHeaders());
				if (contentLength > 0) {
					httpHeaders.setContentLength(contentLength);
				}
				else {
					httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
				}
				return httpHeaders;
			}

			@Override
			public Flux<DataBuffer> getBody() {
				return outputMessage.getBody();
			}
		};
	}

}
