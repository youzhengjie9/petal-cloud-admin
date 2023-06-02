package com.petal.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


/**
 * 网关配置属性
 *
 * @author youzhengjie
 * @date 2023/05/17 09:45:30
 */
@Data
@ConfigurationProperties("gateway")
public class GatewayConfigProperties {

	/**
	 * 网关不需要校验验证码的客户端
	 */
	private List<String> ignoreCheckCaptchaClients;

}
