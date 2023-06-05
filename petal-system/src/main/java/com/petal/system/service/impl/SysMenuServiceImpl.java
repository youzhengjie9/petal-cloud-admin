package com.petal.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petal.common.base.utils.SnowId;
import com.petal.common.base.dto.SysMenuDTO;
import com.petal.common.base.entity.SysMenu;
import com.petal.system.mapper.SysMenuMapper;
import com.petal.system.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单服务impl
 *
 * @author youzhengjie
 * @date 2022/10/17 23:22:12
 */
@Service
@Slf4j
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private SysMenuMapper sysMenuMapper;

    //顶层菜单名称
    private static final String TOP_MENU_NAME="顶层菜单";

    @Autowired
    public void setSysMenuMapper(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    @Override
    public List<SysMenu> getMenuListByUserId(long userId) {
        return sysMenuMapper.getMenuListByUserId(userId);
    }

    @Override
    public List<String> getUserPermissionByUserId(long userid) {
        return sysMenuMapper.getUserPermissionByUserId(userid);
    }

    @Override
    public List<SysMenu> getAllMenuPermission() {
        return sysMenuMapper.getAllMenuPermission();
    }

    @Override
    public List<SysMenu> getAssignMenuTreePermission() {
        return sysMenuMapper.getAssignMenuTreePermission();
    }

    @Override
    public List<SysMenu> selectRoleCheckedMenuByRoleId(long roleid) {

        return sysMenuMapper.selectRoleCheckedMenuByRoleId(roleid);
    }

    @Override
    public List<SysMenu> onlySelectDirectory() {
        return sysMenuMapper.onlySelectDirectory();
    }

    @Override
    public List<SysMenu> onlySelectMenu() {
        return sysMenuMapper.onlySelectMenu();
    }

    @Override
    public String selectMenuNameByMenuId(long menuid) {

        return sysMenuMapper.selectMenuNameByMenuId(menuid);
    }

    @Override
    public String getRouterByUserId(long userid) {
        List<SysMenu> router = sysMenuMapper.getRouterByUserId(userid);
        //这个代码十分重要，解决登陆时，前端因为有些用户没有菜单/路由（也就是这个getRouterByUserId方法查不到数据导致一直死循环）
        //设置一个默认的路由，不管是什么用户、有没有菜单都会有这个默认的路由。防止前端死循环
        SysMenu defaultRouter = SysMenu.builder()
                .path("/dashboard")
                .component("/dashboard/index")
                .build();
        router.add(0,defaultRouter);
        return JSON.toJSONString(router);
    }

    @Override
    public int addMenu(SysMenuDTO sysMenuDto) {

        SysMenu sysMenu = sysMenuDto.getSysMenu();
        //生成分布式id
        sysMenu.setId(SnowId.nextId());
        //设置parentid
        sysMenu.setParentId(sysMenuDto.getParentId());
        //菜单类型
        sysMenu.setType(sysMenuDto.getMenuType());
        //创建时间
        sysMenu.setCreateTime(LocalDateTime.now());
        //修改时间
        sysMenu.setUpdateTime(LocalDateTime.now());

        return sysMenuMapper.addMenu(sysMenu);
    }

    @Override
    public int updateMenu(SysMenuDTO sysMenuDto) {

        SysMenu sysMenu = sysMenuDto.getSysMenu();
        //设置parentid
        sysMenu.setParentId(sysMenuDto.getParentId());
        //菜单类型
        sysMenu.setType(sysMenuDto.getMenuType());
        //修改时间
        sysMenu.setUpdateTime(LocalDateTime.now());

        return sysMenuMapper.updateMenu(sysMenu);
    }

    @Override
    public int deleteMenu(long menuid) {
        LambdaQueryWrapper<SysMenu> menuLambdaQueryWrapper = new LambdaQueryWrapper<>();
        menuLambdaQueryWrapper.eq(SysMenu::getId,menuid);
        return sysMenuMapper.delete(menuLambdaQueryWrapper);
    }

}
