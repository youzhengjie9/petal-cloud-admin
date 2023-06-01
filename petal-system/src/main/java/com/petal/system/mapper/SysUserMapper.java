package com.petal.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.petal.oauth2.common.base.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {

}
