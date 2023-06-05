package com.petal.system.service;

import javax.servlet.http.HttpServletResponse;

/**
 * 导出excel服务
 *
 * @author youzhengjie
 * @date 2022/10/22 11:18:19
 */
public interface SysExportExcelService {


    /**
     * 导出所有用户
     *
     * @param response       响应
     */
    void exportAllUser(HttpServletResponse response);

    /**
     * 导出所有角色
     *
     * @param response       响应
     */
    void exportAllRole(HttpServletResponse response);

    /**
     * 导出所有菜单
     *
     * @param response       响应
     */
    void exportAllMenu(HttpServletResponse response);

    /**
     * 导出所有登录日志
     *
     * @param response 响应
     */
    void exportAllLoginLog(HttpServletResponse response);


    /**
     * 导出所有操作日志
     *
     * @param response 响应
     */
    void exportAllOperationLog(HttpServletResponse response);
}
