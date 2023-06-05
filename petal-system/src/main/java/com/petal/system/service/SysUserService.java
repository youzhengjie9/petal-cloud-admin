package com.petal.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petal.common.base.entity.SysUser;
import com.petal.common.base.dto.SysUserFormDTO;
import com.petal.common.base.entity.SysUserRole;

import java.util.List;

public interface SysUserService extends IService<SysUser> {

    /**
     * 查询所有用户并分页（并对分页功能进行性能调优）
     */
    List<SysUser> selectAllUserByLimit(int page, int size);

    /**
     * 查询总用户数
     */
    int selectAllUserCount();

    /**
     * 添加用户
     *
     * @param sysUserFormDto 用户表单dto
     * @return int
     */
    int addUser(SysUserFormDTO sysUserFormDto);

    /**
     * 更新用户
     *
     * @param sysUserFormDto 用户表单dto
     * @return int
     */
    int updateUser(SysUserFormDTO sysUserFormDto);


    /**
     * 删除用户
     *
     * @param id id
     * @return int
     */
    boolean deleteUser(long id);

    /**
     * 删除用户所有角色
     *
     * @param userid 用户标识
     * @return int
     */
    int deleteUserAllRoles(long userid);

    /**
     * 将角色分配给用户
     *
     * @param sysUserRoleList 用户角色列表
     * @return boolean
     */
    boolean assignRoleToUser(List<SysUserRole> sysUserRoleList);

    /**
     * 给用户添加角色
     *
     * @param sysUserRoleList 用户角色列表
     * @return int
     */
    int addRoleToUser(List<SysUserRole> sysUserRoleList);

    /**
     * mysql通过userName关键字搜索（后期为了性能要放到elasticsearch中，mysql速度不高）
     */
    List<SysUser> searchUserByUserNameAndLimit(String userName,
                                            int page,
                                            int size);


    /**
     * 按用户名搜索用户数量
     *
     * @param userName 用户名
     * @return int
     */
    int searchUserCountByUserName(String userName);


}
