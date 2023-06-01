package com.petal.common.base.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * 登录类型枚举
 *
 * @author youzhengjie
 * @date 2023/05/08 22:40:44
 */
@Getter
@RequiredArgsConstructor
public enum LoginTypeEnum {

	/**
	 * 账号密码登录
	 */
	PWD("PWD", "账号密码登录"),

	/**
	 * 手机验证码登录
	 */
	SMS("SMS", "手机验证码登录");

	/**
	 * 类型
	 */
	private final String type;

	/**
	 * 描述
	 */
	private final String description;

}
