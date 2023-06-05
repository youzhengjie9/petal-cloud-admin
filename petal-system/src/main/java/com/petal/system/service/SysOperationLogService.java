package com.petal.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petal.common.base.entity.SysOperationLog;

import java.util.List;

/**
 * 操作日志服务
 *
 * @author youzhengjie
 * @date 2022/10/21 23:32:14
 */
public interface SysOperationLogService extends IService<SysOperationLog> {

    /**
     * 查询所有操作日志并分页
     *
     * @param page 页面
     * @param size 大小
     * @return {@link List}<{@link SysOperationLog}>
     */
    List<SysOperationLog> selectAllOperationLogByLimit(int page, int size);


    long selectAllOperationLogCount();

    /**
     * 添加操作日志到elasticsearch
     *
     * @param sysOperationLog 操作日志
     * @return boolean
     */
    boolean addOperationLogToEs(SysOperationLog sysOperationLog);

    /**
     * 根据id删除elasticsearch中的操作日志
     *
     * @param id id
     * @return boolean
     */
    boolean deleteOperationLogToEs(Long id);


    /**
     * 更新elasticsearch中的操作日志
     *
     * @param sysOperationLog 操作日志
     * @return boolean
     */
    boolean updateOperationLogToEs(SysOperationLog sysOperationLog);

    /**
     * 根据username分页查询操作日志
     *
     * @param username 用户名
     * @param page     页面
     * @param size     大小
     * @return {@link List}<{@link SysOperationLog}>
     */
    List<SysOperationLog> searchOperationLogByUserNameAndLimit(String username, int page, int size);

    /**
     * 根据username查询符合条件的操作日志数量
     *
     * @param username 用户名
     * @return int
     */
    long searchOperationLogCountByUserName(String username);
}
