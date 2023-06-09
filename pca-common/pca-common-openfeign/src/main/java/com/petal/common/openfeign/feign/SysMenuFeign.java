package com.petal.common.openfeign.feign;

import com.petal.common.base.constant.Oauth2Constant;
import com.petal.common.base.dto.SysMenuDTO;
import com.petal.common.base.entity.SysMenu;
import com.petal.common.base.utils.ResponseResult;
import com.petal.common.openfeign.constant.ApplicationNameConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@FeignClient(value = ApplicationNameConstant.PETAL_SYSTEM_SERVER,path = "/sys/menu")
public interface SysMenuFeign {

    @GetMapping(path = "/selectRoleCheckedMenuByRoleId")
    public ResponseResult<List<SysMenu>> selectRoleCheckedMenuByRoleId(@RequestParam("id") String id);

    @PostMapping("/addMenu")
    public ResponseResult<Object> addMenu(@RequestBody @Valid SysMenuDTO sysMenuDTO);

    @PostMapping("/updateMenu")
    public ResponseResult<Object> updateMenu(@RequestBody @Valid SysMenuDTO sysMenuDTO);

    @DeleteMapping(path = "/deleteMenu")
    public ResponseResult<Object> deleteMenu(@RequestParam("menuid") long menuid);

    @GetMapping(path = "/selectMenuNameByMenuId")
    public ResponseResult<String> selectMenuNameByMenuId(@RequestParam("menuid") long menuid);

    @GetMapping(path = "/getUserPermissionByUserId")
    public ResponseResult<List<String>> getUserPermissionByUserId(@RequestParam("userid") long userid,
                                                                  @RequestHeader(Oauth2Constant.ONLY_FEIGN_CALL_HEADER_NAME)
                                                                  String onlyFeignCallHeader);
}
