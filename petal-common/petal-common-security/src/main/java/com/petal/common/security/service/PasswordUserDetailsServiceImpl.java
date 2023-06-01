package com.petal.common.security.service;

import com.petal.common.base.entity.SysUser;
import com.petal.common.base.utils.ResponseResult;
import com.petal.common.openfeign.feign.SysUserFeign;
import com.petal.common.security.dto.UserInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 帐号密码模式下的UserDetailsService实现类
 *
 * @author youzhengjie
 * @date 2023/05/13 23:49:00
 */
@Slf4j
@Primary
public class PasswordUserDetailsServiceImpl implements CustomUserDetailsService {

	private SysUserFeign sysUserFeign;

	@Autowired
	public void setSysUserFeign(SysUserFeign sysUserFeign) {
		this.sysUserFeign = sysUserFeign;
	}

	/**
	 * 通过用户名构建UserDetails对象
	 *
	 * @param username 用户名
	 * @return
	 */
	@Override
	@SneakyThrows
	public UserDetails loadUserByUsername(String username) {
		ResponseResult<SysUser> sysUserResponseResult =
				sysUserFeign.queryUserByUserName(username,"123");

		UserInfo userInfo = new UserInfo();
		userInfo.setSysUser(sysUserResponseResult.getData());
		// TODO: 2023/5/6 模拟权限
		userInfo.setPermissions(new String[]{"sys:test3","sys:test5"});

		return getUserDetails(ResponseResult.ok(userInfo));
	}

	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}

}
