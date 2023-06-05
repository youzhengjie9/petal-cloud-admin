package com.petal.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petal.common.base.entity.SysUser;
import com.petal.common.base.dto.SysUserFormDTO;
import com.petal.common.base.entity.SysUserRole;
import com.petal.system.mapper.SysUserMapper;
import com.petal.system.service.SysUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private SysUserMapper sysUserMapper;

    @Autowired
    public void setSysUserMapper(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public List<SysUser> selectAllUserByLimit(int page, int size) {
        return sysUserMapper.selectAllUserByLimit(page, size);
    }

    @Override
    public int selectAllUserCount() {
        return sysUserMapper.selectAllUserCount();
    }

    @Override
    public int addUser(SysUserFormDTO sysUserFormDto) {

        SysUser user = new SysUser();
        BeanUtils.copyProperties(sysUserFormDto,user);

        user.setStatus(sysUserFormDto.getStatus() ?0:1);

        if("男".equals(sysUserFormDto.getSex())){
            user.setSex(0);
        }else if("女".equals(sysUserFormDto.getSex())){
            user.setSex(1);
        }else{
            user.setSex(2);
        }
        //然后再补充一些前端没有传过来的属性
        user.setCreateTime(LocalDate.now());
        user.setUpdateTime(LocalDateTime.now());

        return sysUserMapper.insert(user);
    }

    @Override
    public int updateUser(SysUserFormDTO sysUserFormDto) {

        SysUser user = new SysUser();
        BeanUtils.copyProperties(sysUserFormDto,user);

        user.setStatus(sysUserFormDto.getStatus() ?0:1);

        if("男".equals(sysUserFormDto.getSex())){
            user.setSex(0);
        }else if("女".equals(sysUserFormDto.getSex())){
            user.setSex(1);
        }else{
            user.setSex(2);
        }
        //然后再补充一些前端没有传过来的属性
        user.setUpdateTime(LocalDateTime.now());
        return sysUserMapper.updateUser(user);
    }

    @Override
    public boolean deleteUser(long id) {

        try {
            //删除用户
            LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SysUser::getId,id);
            sysUserMapper.delete(lambdaQueryWrapper);
            //删除用户所拥有的所有角色
            sysUserMapper.deleteUserAllRoles(id);
            return true;
        }catch (Exception e){
            throw new RuntimeException("删除用户失败");
        }
    }

    @Override
    public int deleteUserAllRoles(long userid) {
        return sysUserMapper.deleteUserAllRoles(userid);
    }


    /**
     * 将角色分配给用户
     *
     * @param sysUserRoleList 用户角色列表
     * @return boolean
     */
    @Override
    public boolean assignRoleToUser(List<SysUserRole> sysUserRoleList) {
        try {
            //先删除用户所有角色
            sysUserMapper.deleteUserAllRoles(sysUserRoleList.get(0).getUserId());
            //再把所有新的角色（包括以前选过的）都重新添加到数据库中
            sysUserMapper.addRoleToUser(sysUserRoleList);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("assignRoleToUser异常,事务已回滚。");
        }
    }

    @Override
    public int addRoleToUser(List<SysUserRole> sysUserRoleList) {
        return sysUserMapper.addRoleToUser(sysUserRoleList);
    }

    @Override
    public List<SysUser> searchUserByUserNameAndLimit(String userName, int page, int size) {
        return sysUserMapper.searchUserByUserNameAndLimit(userName, page, size);
    }

    @Override
    public int searchUserCountByUserName(String userName) {
        return sysUserMapper.searchUserCountByUserName(userName);
    }


}
