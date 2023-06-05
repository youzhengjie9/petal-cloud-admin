package com.petal.common.base.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.petal.common.base.entity.SysMenu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 菜单dto
 *
 * @author youzhengjie
 * @date 2022/10/18 16:10:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class SysMenuDTO implements Serializable {

    private SysMenu sysMenu;

    //所属菜单id（也就是父菜单id）
    private Long parentId;

    //新增/修改菜单的类型（0目录、1菜单、2按钮）
    private int menuType;

}
