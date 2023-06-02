package com.petal.gateway.exception;

/**
 * 检查验证码异常
 *
 * @author youzhengjie
 * @date 2023/05/17 10:43:26
 */
public class CheckCaptchaException extends RuntimeException {

	private static final long serialVersionUID = -7285211528095468156L;

	public CheckCaptchaException() {
	}

	public CheckCaptchaException(String msg) {
		super(msg);
	}

}
