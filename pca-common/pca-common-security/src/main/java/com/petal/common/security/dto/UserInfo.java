package com.petal.common.security.dto;

import com.petal.common.base.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

	/**
	 * 用户基本信息
	 */
	private SysUser sysUser;

	/**
	 * 权限标识集合
	 */
	private String[] permissions;

//	/**
//	 * 角色集合
//	 */
//	private Long[] roles;
//
//	/**
//	 * 角色集合
//	 */
//	private List<SysRole> roleList;


}
