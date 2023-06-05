package com.petal.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petal.common.base.dto.SysRoleFormDTO;
import com.petal.common.base.entity.SysRole;
import com.petal.common.base.entity.SysRoleMenu;

import java.util.List;

/**
 * 角色服务
 *
 * @author youzhengjie
 * @date 2022/10/13 12:13:21
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 查询所有角色并分页（并对分页功能进行性能调优）
     */
    List<SysRole> selectAllRoleByLimit(int page, int size);

    /**
     * 查询总角色数
     */
    int selectAllRoleCount();

    /**
     * 查询所有角色
     */
    List<SysRole> selectAllRole();

    /**
     * 通过userid来查询指定用户当前所拥有的role角色列表
     *
     * @param userid 用户标识
     * @return {@link List}<{@link SysRole}>
     */
    List<SysRole> selectUserCheckedRoleByUserId(long userid);

    /**
     * 添加角色
     *
     * @param sysRoleFormDto 角色表单dto
     * @return int
     */
    int addRole(SysRoleFormDTO sysRoleFormDto);

    /**
     * 更新角色
     *
     * @param sysRoleFormDto 角色表单dto
     * @return int
     */
    int updateRole(SysRoleFormDTO sysRoleFormDto);


    /**
     * 删除角色
     *
     * @param id id
     * @return int
     */
    boolean deleteRole(long id);

    /**
     * 给角色分配菜单权限
     * @param sysRoleMenuList 角色菜单列表
     * @return boolean
     */
    boolean assignMenuToRole(List<SysRoleMenu> sysRoleMenuList);

    /**
     * mysql通过role的name关键字搜索
     *
     * @param roleName 角色名
     * @param page     页面
     * @param size     大小
     * @return {@link List}<{@link SysRole}>
     */
    List<SysRole> searchRoleByRoleNameAndLimit(String roleName, int page, int size);

    /**
     * 按role的name搜索role数量
     *
     * @param roleName 角色名
     * @return int
     */
    int searchRoleCountByRoleName(String roleName);
}
