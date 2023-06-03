package com.petal.common.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;


/**
 * Oauth2客户端信息
 *
 * @author youzhengjie
 * @date 2023/05/16 23:53:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_oauth2_client")
public class SysOauth2Client implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 客户端id
	 */
	@NotBlank(message = "client_id 不能为空")
	@TableId(value = "client_id", type = IdType.INPUT)
	private String clientId;

	/**
	 * 客户端密钥,作用是和客户端id进行配对（格式为 base64加密clientId:clientSecret）
	 */
	@NotBlank(message = "client_secret 不能为空")
	private String clientSecret;

	/**
	 * 资源id集合
	 */
	private String resourceIds;

	/**
	 * 作用域
	 */
	@NotBlank(message = "scope 不能为空")
	private String scope;

	/**
	 * 该client所支持的grant_type类型(例如:密码登录(password)、手机号登录(sms_login)等等),例如想要密码登录,该grant_type就必须包含password类型
	 */
	private String authorizedGrantTypes;

	/**
	 * 回调地址(当授权码模式才生效)
	 */
	private String webServerRedirectUri;

	/**
	 * 权限
	 */
	private String authorities;

	/**
	 * 请求令牌有效时间
	 */
	private Integer accessTokenValidity;

	/**
	 * 刷新令牌有效时间
	 */
	private Integer refreshTokenValidity;

	/**
	 * 扩展信息
	 */
	private String additionalInformation;

	/**
	 * 是否自动放行
	 */
	private String autoapprove;

}
