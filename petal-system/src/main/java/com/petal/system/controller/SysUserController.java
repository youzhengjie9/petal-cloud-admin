package com.petal.system.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.petal.common.base.dto.SysAssignRoleDTO;
import com.petal.common.base.dto.SysUserFormDTO;
import com.petal.common.base.entity.SysRole;
import com.petal.common.base.entity.SysUser;
import com.petal.common.base.entity.SysUserRole;
import com.petal.common.base.enums.ResponseType;
import com.petal.common.base.utils.ResponseResult;
import com.petal.common.base.utils.SnowId;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
    @GetMapping("/getCurrentUserInfo")
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

    /**
     * 查询所有用户数量
     *
     * @return {@link ResponseResult}
     */
    @PreAuthorize("@pms.hasPermission('sys:user:list')")
    @GetMapping(path = "/selectAllUserCount")
    @ApiOperation("查询所有用户数量")
    public ResponseResult<Integer> selectAllUserCount(){
        try {
            int count = sysUserService.selectAllUserCount();
            return ResponseResult.build(ResponseType.SUCCESS.getCode(),
                    ResponseType.SUCCESS.getMessage(),count);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(),
                    ResponseType.ERROR.getMessage(),null);
        }
    }

    /**
     *
     * @param page
     * @param size
     * @return
     */
    //1 8 = 0 8
    //2 8 = 8 8
    //3 8 = 16 8
    @PreAuthorize("@pms.hasPermission('sys:user:list')")
    @OperLog("查询所有用户并分页")
    @GetMapping(path = "/selectAllUserByLimit")
    @ApiOperation("查询所有用户并分页")
    public ResponseResult<List<SysUser>> selectAllUserByLimit(int page,int size){
        page=(page-1)*size;
        try {
            List<SysUser> sysUsers = sysUserService.selectAllUserByLimit(page, size);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(),
                    ResponseType.SUCCESS.getMessage(),sysUsers);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(),
                    ResponseType.ERROR.getMessage(),null);
        }
    }

    /**
     * 添加用户
     *
     * @param sysUserFormDTO 用户表单dto
     * @return {@link ResponseResult}
     */
    @PreAuthorize("@pms.hasPermission('sys:user:list:add')")
    @OperLog("添加用户")
    @PostMapping("/addUser")
    @ApiOperation("添加用户")
    public ResponseResult<Object> addUser(@RequestBody @Valid SysUserFormDTO sysUserFormDTO){
        try {
            //将密码进行加密
            String encodePassword = passwordEncoder.encode(sysUserFormDTO.getPassword());
            sysUserFormDTO.setPassword(encodePassword);
            sysUserService.addUser(sysUserFormDTO);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(),
                    ResponseType.SUCCESS.getMessage(),null);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(),
                    ResponseType.ERROR.getMessage(),null);
        }
    }

    /**
     * 修改用户
     *
     * @param sysUserFormDTO 用户表单dto
     * @return {@link ResponseResult}
     */
    @PreAuthorize("@pms.hasPermission('sys:user:list:update')")
    @OperLog("修改用户")
    @PostMapping(path = "/updateUser")
    @ApiOperation("修改用户")
    public ResponseResult<Object> updateUser(@RequestBody @Valid SysUserFormDTO sysUserFormDTO){
        try {
            //如果密码不为空，则进行加密再存储到数据库中
            if(StringUtils.hasText(sysUserFormDTO.getPassword())){
                //将密码进行加密
                String encodePassword = passwordEncoder.encode(sysUserFormDTO.getPassword());
                sysUserFormDTO.setPassword(encodePassword);
            }
            sysUserService.updateUser(sysUserFormDTO);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(),
                    ResponseType.SUCCESS.getMessage(),null);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(),
                    ResponseType.ERROR.getMessage(),null);
        }
    }

    /**
     * 删除用户
     *
     * @param id id
     * @return {@link ResponseResult}
     */
    @PreAuthorize("@pms.hasPermission('sys:user:list:delete')")
    @OperLog("删除用户")
    @DeleteMapping(path = "/deleteUser")
    @ApiOperation("删除用户")
    public ResponseResult<Object> deleteUser(@RequestParam("id") long id){
        try {
            sysUserService.deleteUser(id);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(),
                    ResponseType.SUCCESS.getMessage(),null);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(),
                    ResponseType.ERROR.getMessage(),null);
        }

    }

    /**
     * 分配角色
     * 前端通过这个格式来传给这个接口：let jsonData={
     *         roles:this.assignRoleSelectedList,
     *         userid:this.currentAssignRoleUserId
     *       }
     * @param sysAssignRoleDTO 分配角色dto
     * @return {@link ResponseResult}
     */
    @PreAuthorize("@pms.hasPermission('sys:user:list:assign-role')")
    @OperLog("分配角色")
    @PostMapping(path = "/assignRole")
    @ApiOperation("分配角色")
    public ResponseResult<Object> assignRole(@RequestBody @Valid SysAssignRoleDTO sysAssignRoleDTO){

        try {
            if(sysAssignRoleDTO.getSysRoles()==null || sysAssignRoleDTO.getSysRoles().size()==0){
                return ResponseResult.build(ResponseType.SUCCESS.getCode(),
                        ResponseType.SUCCESS.getMessage(),null);
            }
            //通过stream流把role的id组成一个新的集合
            List<Long> roleIds = sysAssignRoleDTO
                    .getSysRoles()
                    .stream()
                    .map(SysRole::getId)
                    //要进行去重
                    .distinct()
                    .collect(Collectors.toList());
            long userid = Long.parseLong(sysAssignRoleDTO.getUserid());

            List<SysUserRole> sysUserRoleList=new CopyOnWriteArrayList<>();
            for (Long roleId : roleIds) {
                SysUserRole sysUserRole = SysUserRole
                        .builder()
                        //手动使用雪花算法生成分布式id
                        .id(SnowId.nextId())
                        .roleId(roleId)
                        .userId(userid)
                        .build();
                sysUserRoleList.add(sysUserRole);
            }
            //调用分配角色业务类
            sysUserService.assignRoleToUser(sysUserRoleList);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(),
                    ResponseType.SUCCESS.getMessage(),null);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(),
                    ResponseType.ERROR.getMessage(),null);
        }

    }


    /**
     * mysql通过userName关键字搜索
     *
     * @param userName 用户名
     * @param page     页面
     * @param size     大小
     * @return {@link ResponseResult}
     */
    @PreAuthorize("@pms.hasPermission('sys:user:list')")
    @OperLog("根据用户名搜索用户并分页")
    @GetMapping(path = "/searchUserByUserNameAndLimit")
    @ApiOperation("根据用户名搜索用户并分页")
    public ResponseResult<List<SysUser>> searchUserByUserNameAndLimit(@RequestParam("userName") String userName,
                                                       @RequestParam("page") int page,
                                                       @RequestParam("size") int size){
        page=(page-1)*size;
        try {
            List<SysUser> sysUsers = sysUserService.searchUserByUserNameAndLimit(userName, page, size);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(),
                    ResponseType.SUCCESS.getMessage(),sysUsers);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(),
                    ResponseType.ERROR.getMessage(),null);
        }
    }

    /**
     * 按用户名搜索用户数量
     *
     * @param userName 用户名
     * @return {@link ResponseResult}
     */
    @PreAuthorize("@pms.hasPermission('sys:user:list')")
    @GetMapping(path = "/searchUserCountByUserName")
    @ApiOperation("按用户名搜索用户数量")
    public ResponseResult<Integer> searchUserCountByUserName(@RequestParam("userName") String userName){

        try {
            int count = sysUserService.searchUserCountByUserName(userName);
            return ResponseResult.build(ResponseType.SUCCESS.getCode(),
                    ResponseType.SUCCESS.getMessage(),count);
        }catch (Exception e){
            return ResponseResult.build(ResponseType.ERROR.getCode(),
                    ResponseType.ERROR.getMessage(),null);
        }
    }

}
