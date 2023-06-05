package com.petal.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petal.common.base.dto.SysRoleFormDTO;
import com.petal.common.base.entity.SysRole;
import com.petal.common.base.entity.SysRoleMenu;
import com.petal.system.mapper.SysRoleMapper;
import com.petal.system.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色服务impl
 *
 * @author youzhengjie
 * @date 2022/10/17 23:23:21
 */
@Service
@Slf4j
@Transactional //开启事务
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private SysRoleMapper sysRoleMapper;

    @Autowired
    public void setSysRoleMapper(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public List<SysRole> selectAllRoleByLimit(int page, int size) {
        return sysRoleMapper.selectAllRoleByLimit(page, size);
    }

    @Override
    public int selectAllRoleCount() {
        return sysRoleMapper.selectAllRoleCount();
    }

    @Override
    public List<SysRole> selectAllRole() {
        return sysRoleMapper.selectAllRole();
    }

    @Override
    public List<SysRole> selectUserCheckedRoleByUserId(long userid) {
        return sysRoleMapper.selectUserCheckedRoleByUserId(userid);
    }

    @Override
    public int addRole(SysRoleFormDTO sysRoleFormDto) {

        // TODO: 2022/10/27
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleFormDto, sysRole);

        if(sysRoleFormDto.getStatus()){
            sysRole.setStatus(0);
        }else {
            sysRole.setStatus(1);
        }
        //然后再补充一些前端没有传过来的属性
        sysRole.setCreateTime(LocalDateTime.now());
        sysRole.setUpdateTime(LocalDateTime.now());

        //只有调用mybatis-plus内置的sql方法才会自动生成分布式主键id
        return sysRoleMapper.insert(sysRole);
    }

    @Override
    public int updateRole(SysRoleFormDTO sysRoleFormDto) {

        // TODO: 2022/10/27
        SysRole sysRole = new SysRole();
        BeanUtils.copyProperties(sysRoleFormDto, sysRole);

        if(sysRoleFormDto.getStatus()){
            sysRole.setStatus(0);
        }else {
            sysRole.setStatus(1);
        }
        //然后再补充一些前端没有传过来的属性
        sysRole.setUpdateTime(LocalDateTime.now());

        LambdaQueryWrapper<SysRole> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roleLambdaQueryWrapper.eq(SysRole::getId, sysRole.getId());
        return sysRoleMapper.update(sysRole,roleLambdaQueryWrapper);
    }

    @Override
    public boolean deleteRole(long id) {

        try {
            //删除角色
            LambdaQueryWrapper<SysRole> roleLambdaQueryWrapper = new LambdaQueryWrapper<>();
            roleLambdaQueryWrapper.eq(SysRole::getId,id);
            sysRoleMapper.delete(roleLambdaQueryWrapper);
            //删除角色对应的所有菜单
            sysRoleMapper.deleteRoleAllMenu(id);
            return true;
        }catch (Exception e){
            throw new RuntimeException("删除角色失败");
        }
    }

    @Override
    public boolean assignMenuToRole(List<SysRoleMenu> sysRoleMenuList) {

        try {
            //先删除角色对应的所有菜单
            sysRoleMapper.deleteRoleAllMenu(sysRoleMenuList.get(0).getRoleId());
            //再把所有新的菜单（包括以前选过的）都重新添加到数据库中
            sysRoleMapper.addMenuToRole(sysRoleMenuList);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("assignMenuToRole异常,事务已回滚。");
        }

    }

    @Override
    public List<SysRole> searchRoleByRoleNameAndLimit(String roleName, int page, int size) {
        return sysRoleMapper.searchRoleByRoleNameAndLimit(roleName, page, size);
    }

    @Override
    public int searchRoleCountByRoleName(String roleName) {
        return sysRoleMapper.searchRoleCountByRoleName(roleName);
    }
}
