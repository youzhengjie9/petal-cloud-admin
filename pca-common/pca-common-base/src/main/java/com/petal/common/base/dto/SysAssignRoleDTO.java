package com.petal.common.base.dto;

import com.petal.common.base.entity.SysRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 分配角色dto
 *
 * @author youzhengjie
 * @date 2022/10/16 22:21:30
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
@Data
public class SysAssignRoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("该用户新的角色列表")
    private List<SysRole> sysRoles;

    @NotBlank(message = "需要分配角色的用户id不能为空")
    private String userid;

}
