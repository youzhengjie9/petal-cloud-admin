package com.petal.common.security.service;

import com.petal.common.base.constant.Oauth2Constant;
import com.petal.common.base.entity.SysUser;
import com.petal.common.base.utils.ResponseResult;
import com.petal.common.openfeign.feign.SysMenuFeign;
import com.petal.common.openfeign.feign.SysUserFeign;
import com.petal.common.security.dto.UserInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * 手机验证码模式下的UserDetailsService实现类
 *
 * @author youzhengjie
 * @date 2023/05/13 00:23:44
 */
@Slf4j
public class SmsUserDetailsServiceImpl implements CustomUserDetailsService {

	private SysUserFeign sysUserFeign;

	private SysMenuFeign sysMenuFeign;

	@Autowired
	public void setSysUserFeign(SysUserFeign sysUserFeign) {
		this.sysUserFeign = sysUserFeign;
	}

	@Autowired
	public void setSysMenuFeign(SysMenuFeign sysMenuFeign) {
		this.sysMenuFeign = sysMenuFeign;
	}

	/**
	 * 手机号登录
	 * @param phone 手机号
	 * @return
	 */
	@Override
	@SneakyThrows
	public UserDetails loadUserByUsername(String phone) {

		ResponseResult<SysUser> sysUserResponseResult = sysUserFeign.queryUserByPhone(phone,"123456");

		UserInfo userInfo = new UserInfo();
		userInfo.setSysUser(sysUserResponseResult.getData());

		//查询用户菜单权限（就是查询Menu类中type=1和2的菜单权限标识perms，但是不包括type=0），并放到loginUser中返回
		ResponseResult<List<String>> permissionResponseResult =
				sysMenuFeign.getUserPermissionByUserId(sysUserResponseResult.getData().getId(), "123");
		List<String> permissions = permissionResponseResult.getData();
		if (permissions == null || permissions.size() == 0) {
			userInfo.setPermissions(new String[]{});
		}else {
			String[] perms = new String[permissions.size()];
			for (int i = 0; i < permissions.size(); i++) {
				perms[i]=permissions.get(i);
			}
			userInfo.setPermissions(perms);
		}

		return getUserDetails(ResponseResult.ok(userInfo));
	}


	@Override
	public UserDetails loadUserBySecurityOauth2User(SecurityOauth2User securityOauth2User) {
		return this.loadUserByUsername(securityOauth2User.getPhone());
	}

	/**
	 * 是否支持此客户端校验
	 * @param clientId 目标客户端
	 * @return true/false
	 */
	@Override
	public boolean support(String clientId, String grantType) {
		return Oauth2Constant.SMS_GRANT_TYPE.equals(grantType);
	}

}
