package com.petal.common.base.vo;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 封装Token
 * @author youzhengjie 2022-10-01 23:46:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
@Builder
public class TokenVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;

    private String nickName;

    private String avatar;

    private String accessToken;

    private String refreshToken;
    //该用户的动态菜单（侧边栏）
    private String dynamicMenu;
    //该用户的动态路由
    private String dynamicRouter;
    //用户权限perm
    private String perm;


}
