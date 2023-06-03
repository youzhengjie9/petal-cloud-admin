package com.petal.common.security.resource.server;

import cn.hutool.extra.spring.SpringUtil;
import com.petal.common.base.constant.Oauth2Constant;
import com.petal.common.security.service.CustomUserDetailsService;
import com.petal.common.security.service.SecurityOauth2User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.security.Principal;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * 自定义token自省器
 * <p>
 * 作用: 携带token去访问认证服务端的token自省端点（/oauth2/introspect）,获取对应的有效用户信息，验证token并返回其属性，表明验证成功。
 *
 * @author youzhengjie
 * @date 2023/05/12 12:18:11
 */
@Slf4j
public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

	private final OAuth2AuthorizationService authorizationService;

	public CustomOpaqueTokenIntrospector(OAuth2AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		// 根据token从Redis中查询认证信息（这个是旧的认证信息）
		OAuth2Authorization oldAuthorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
		if (Objects.isNull(oldAuthorization)) {
			throw new InvalidBearerTokenException(token);
		}

		// 客户端模式默认返回
		if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(oldAuthorization.getAuthorizationGrantType())) {
			return new DefaultOAuth2AuthenticatedPrincipal(oldAuthorization.getPrincipalName(),
					oldAuthorization.getAttributes(), AuthorityUtils.NO_AUTHORITIES);
		}

		Map<String, CustomUserDetailsService> userDetailsServiceMap = SpringUtil
			.getBeansOfType(CustomUserDetailsService.class);

		// =============使用CustomUserDetailsService再次查询最新的认证信息（这个是最新的认证信息）================
		Optional<CustomUserDetailsService> optional = userDetailsServiceMap.values()
			.stream()
			.filter(service -> service.support(Objects.requireNonNull(oldAuthorization).getRegisteredClientId(),
					oldAuthorization.getAuthorizationGrantType().getValue()))
			.max(Comparator.comparingInt(Ordered::getOrder));

		UserDetails userDetails = null;
		try {
			Object principal = Objects.requireNonNull(oldAuthorization).getAttributes().get(Principal.class.getName());
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;
			Object tokenPrincipal = usernamePasswordAuthenticationToken.getPrincipal();
			userDetails = optional.get().loadUserBySecurityOauth2User((SecurityOauth2User) tokenPrincipal);

		}
		catch (UsernameNotFoundException notFoundException) {
			log.warn("用户不不存在 {}", notFoundException.getLocalizedMessage());
			throw notFoundException;
		}
		catch (Exception ex) {
			log.error("自省token失败: {}", ex.getLocalizedMessage());
		}
		// ==============================================

		// 注入扩展属性,方便上下文获取客户端ID
		SecurityOauth2User securityOauth2User = (SecurityOauth2User) userDetails;
		Objects.requireNonNull(securityOauth2User)
			.getAttributes()
			.put(Oauth2Constant.CLIENT_ID, oldAuthorization.getRegisteredClientId());
		return securityOauth2User;
	}

}
