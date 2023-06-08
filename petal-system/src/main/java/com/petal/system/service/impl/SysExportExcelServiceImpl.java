package com.petal.system.service.impl;

import com.petal.common.base.entity.*;
import com.petal.common.base.utils.EasyExcelUtil;
import com.petal.system.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 导出excel服务impl
 *
 * @author youzhengjie
 * @date 2022/10/22 11:42:51
 */
@Service
@Slf4j
public class SysExportExcelServiceImpl implements SysExportExcelService {

    private SysUserService sysUserService;

    private SysRoleService sysRoleService;

    private SysMenuService sysMenuService;

    private SysLoginLogService sysLoginLogService;

    private SysOperationLogService sysOperationLogService;

    private EasyExcelUtil easyExcelUtil;

    @Autowired
    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @Autowired
    public void setSysRoleService(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    @Autowired
    public void setSysMenuService(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    @Autowired
    public void setSysLoginLogService(SysLoginLogService sysLoginLogService) {
        this.sysLoginLogService = sysLoginLogService;
    }

    @Autowired
    public void setSysOperationLogService(SysOperationLogService sysOperationLogService) {
        this.sysOperationLogService = sysOperationLogService;
    }

    @Autowired
    public void setEasyExcelUtil(EasyExcelUtil easyExcelUtil) {
        this.easyExcelUtil = easyExcelUtil;
    }

    private static final String EXPORT_USER_PREFIX="用户表";

    private static final String EXPORT_ROLE_PREFIX="角色表";

    private static final String EXPORT_MENU_PREFIX="菜单表";

    private static final String EXPORT_LOGIN_LOG_PREFIX="登录日志表";

    private static final String EXPORT_OPER_LOG_PREFIX="操作日志表";

    /**
     * 导出所有用户
     *
     * @param response       响应
     */
    @Override
    public void exportAllUser(HttpServletResponse response) {

        //查询出数据，后面这些数据会生成excel表
        List<SysUser> sysUserList = sysUserService.lambdaQuery()
                .select(SysUser::getId,
                        SysUser::getUserName,
                        SysUser::getNickName,
                        SysUser::getStatus,
                        SysUser::getEmail,
                        SysUser::getPhone,
                        SysUser::getSex,
                        SysUser::getCreateTime,
                        SysUser::getUpdateTime,
                        SysUser::getDelFlag)
                .list();

        easyExcelUtil.writeExcel(sysUserList,SysUser.class,EXPORT_USER_PREFIX,response);
    }

    /**
     * 导出所有角色
     *
     * @param response       响应
     */
    @Override
    public void exportAllRole(HttpServletResponse response) {

        List<SysRole> sysRoleList = sysRoleService.lambdaQuery()
                .select(SysRole::getId,
                        SysRole::getName,
                        SysRole::getRoleKey,
                        SysRole::getStatus,
                        SysRole::getDelFlag,
                        SysRole::getCreateTime,
                        SysRole::getUpdateTime,
                        SysRole::getRemark)
                .list();

        easyExcelUtil.writeExcel(sysRoleList, SysRole.class,EXPORT_ROLE_PREFIX,response);
    }

    @Override
    public void exportAllMenu(HttpServletResponse response) {

        List<SysMenu> sysMenuList = sysMenuService.lambdaQuery()
                .select(SysMenu::getId,
                        SysMenu::getParentId,
                        SysMenu::getMenuName,
                        SysMenu::getPath,
                        SysMenu::getComponent,
                        SysMenu::getStatus,
                        SysMenu::getVisible,
                        SysMenu::getPerms,
                        SysMenu::getType,
                        SysMenu::getCreateTime,
                        SysMenu::getUpdateTime,
                        SysMenu::getDelFlag,
                        SysMenu::getSort,
                        SysMenu::getRemark)
                .orderByAsc(SysMenu::getSort)
                .list();

        easyExcelUtil.writeExcel(sysMenuList, SysMenu.class,EXPORT_MENU_PREFIX,response);
    }

    @Override
    public void exportAllLoginLog(HttpServletResponse response) {

        List<SysLoginLog> sysLoginLogList = sysLoginLogService.lambdaQuery()
                .select(SysLoginLog::getId,
                        SysLoginLog::getUsername,
                        SysLoginLog::getIp,
                        SysLoginLog::getAddress,
                        SysLoginLog::getBrowser,
                        SysLoginLog::getOs,
                        SysLoginLog::getLoginTime,
                        SysLoginLog::getDelFlag)
                .orderByDesc(SysLoginLog::getLoginTime)
                .list();

        easyExcelUtil.writeExcel(sysLoginLogList, SysLoginLog.class,EXPORT_LOGIN_LOG_PREFIX,response);
    }

    @Override
    public void exportAllOperationLog(HttpServletResponse response) {

        List<SysOperationLog> sysOperationLogList = sysOperationLogService.lambdaQuery()
                .select(SysOperationLog::getId,
                        SysOperationLog::getUsername,
                        SysOperationLog::getType,
                        SysOperationLog::getUri,
                        SysOperationLog::getTime,
                        SysOperationLog::getIp,
                        SysOperationLog::getAddress,
                        SysOperationLog::getBrowser,
                        SysOperationLog::getOs,
                        SysOperationLog::getOperTime,
                        SysOperationLog::getDelFlag)
                .orderByDesc(SysOperationLog::getOperTime)
                .list();
        easyExcelUtil.writeExcel(sysOperationLogList, SysOperationLog.class,EXPORT_OPER_LOG_PREFIX,response);
    }
}
