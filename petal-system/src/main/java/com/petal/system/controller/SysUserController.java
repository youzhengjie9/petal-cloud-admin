package com.petal.system.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.petal.common.base.entity.SysUser;
import com.petal.common.base.enums.ResponseType;
import com.petal.common.base.utils.ResponseResult;
import com.petal.common.security.annotation.PermitAll;
import com.petal.common.security.utils.SecurityUtil;
import com.petal.common.base.annotation.OperLog;
import com.petal.system.service.SysMenuService;
import com.petal.system.service.SysMenuTreeService;
import com.petal.system.service.SysUserService;
import com.petal.common.base.vo.TokenVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户控制器
 *
 * @author youzhengjie
 * @date 2023/05/17 00:00:24
 */
@Api("用户控制器")
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    private SysMenuService sysMenuService;

    private SysMenuTreeService sysMenuTreeService;

    private SysUserService sysUserService;

    @Autowired
    public void setMenuService(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    @Autowired
    public void setMenuTreeService(SysMenuTreeService sysMenuTreeService) {
        this.sysMenuTreeService = sysMenuTreeService;
    }

    @Autowired
    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /**
     * 获取当前用户信息
     * 记住：要携带accessToken
     * @return {@link ResponseResult}
     */
    @OperLog("获取当前用户信息")
    @GetMapping("/user/getCurrentUserInfo")
    @ApiOperation("获取当前用户信息")
    public ResponseResult<TokenVO> getCurrentUserInfo(){

        try {
            // 通过请求头Authorization获取accessToken,从而获取到当前用户的信息
            String username = SecurityUtil.getUsername();
            SysUser sysUser = sysUserService.lambdaQuery().eq(SysUser::getUserName, username).one();

            TokenVO tokenVO = BeanUtil.copyProperties(sysUser, TokenVO.class);
            System.out.println(tokenVO);

            Long userid = sysUser.getId();
            //此处追加一个获取用户动态菜单,生成该用户的动态菜单（由于可能会频繁操作mysql，所以后期可以用缓存技术来减少数据库压力）
            String dynamicMenu = sysMenuTreeService.buildTreeByUserId(userid);
            //由于VUE动态路由刷新会丢失，所以需要再获取获取该用户的所有路由（只包含类型为菜单，type=1的菜单）
            String dynamicRouter = sysMenuService.getRouterByUserId(userid);
            tokenVO.setDynamicMenu(dynamicMenu);
            tokenVO.setDynamicRouter(dynamicRouter);

            //设置用户权限perm
            List<String> userPerm = sysMenuService.getUserPermissionByUserId(userid);
            tokenVO.setPerm(JSON.toJSONString(userPerm));

            return ResponseResult.build(ResponseType.SUCCESS.getCode(), ResponseType.SUCCESS.getMessage(),tokenVO);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(), ResponseType.ERROR.getMessage(),null);
        }
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return {@link ResponseResult}<{@link SysUser}>
     */
    @PermitAll
    @GetMapping(path = "/queryUserByUserName/{username}")
    @ApiOperation("根据用户名查询用户信息")
    public ResponseResult<SysUser> queryUserByUserName(@PathVariable("username") String username){

        return ResponseResult.ok(sysUserService.lambdaQuery().eq(SysUser::getUserName, username).one());
    }

    /**
     * 根据手机号查询用户信息
     *
     * @param phone 电话
     * @return {@link ResponseResult}<{@link SysUser}>
     */
    @PermitAll
    @GetMapping(path = "/queryUserByPhone/{phone}")
    @ApiOperation("根据手机号查询用户信息")
    public ResponseResult<SysUser> queryUserByPhone(@PathVariable("phone") String phone){

        return ResponseResult.ok(sysUserService.lambdaQuery().eq(SysUser::getPhone, phone).one());
    }

}
