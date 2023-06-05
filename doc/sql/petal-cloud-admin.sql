-- 创建数据库
CREATE DATABASE `petal-cloud-admin`;

-- 使用数据库
USE `petal-cloud-admin`;

-- oauth2客户端数据库表

DROP TABLE IF EXISTS `sys_oauth2_client`;
CREATE TABLE `sys_oauth2_client` (
                                            `client_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '客户端id',
                                            `resource_ids` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '资源列表',
                                            `client_secret` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客户端密钥,作用是和客户端id进行配对（格式为 base64加密clientId:clientSecret）',
                                            `scope` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '域',
                                            `authorized_grant_types` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '该client所支持的grant_type类型(例如:密码登录(password)、手机号登录(sms_login)等等),例如想要密码登录,该grant_type就必须包含password类型',
                                            `web_server_redirect_uri` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '回调地址(当授权码模式才生效)',
                                            `authorities` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '角色列表',
                                            `access_token_validity` int DEFAULT NULL COMMENT 'token 有效期',
                                            `refresh_token_validity` int DEFAULT NULL COMMENT '刷新令牌有效期',
                                            `additional_information` varchar(4096) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '令牌扩展字段JSON',
                                            `autoapprove` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否自动放行',
                                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                                            `update_time` datetime DEFAULT NULL COMMENT '更新时间',
                                            `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
                                            `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '更新人',
                                            PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='oauth2客户端表';

-- 模式1: 授权码模式客户端
INSERT INTO `sys_oauth2_client` VALUES ('authorization_login', NULL, 'authorization_login_secret', 'server', 'authorization_code,refresh_token', 'https://www.baidu.com', NULL, NULL, NULL, NULL, 'true', NULL, NULL, NULL, NULL);
-- 模式2: 密码模式客户端
INSERT INTO `sys_oauth2_client` VALUES ('password_login', NULL, 'password_login_secret', 'server', 'password,refresh_token', NULL, NULL, NULL, NULL, NULL, 'true', NULL, NULL, NULL, NULL);
-- 模式3: 短信登录模式客户端
INSERT INTO `sys_oauth2_client` VALUES ('sms_login', NULL, 'sms_login_secret', 'server', 'sms_login,refresh_token', NULL, NULL, NULL, NULL, NULL, 'true', NULL, NULL, NULL, NULL);
-- 模式4: 客户端凭证模式客户端
INSERT INTO `sys_oauth2_client` VALUES ('client_login', NULL, 'client_login_secret', 'server', 'client_credentials', NULL, NULL, NULL, NULL, NULL, 'true', NULL, NULL, NULL, NULL);
-- 测试用的客户端
INSERT INTO `sys_oauth2_client` VALUES ('test', NULL, 'test_secret', 'server', 'password,sms_login,refresh_token', NULL, NULL, NULL, NULL, NULL, 'true', NULL, NULL, NULL, NULL);


-- 经典的RBAC模型的5张数据库表

/* 用户表 */
DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
                            `id` bigint NOT NULL COMMENT '主键',
                            `user_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NULL' COMMENT '用户名',
                            `nick_name` varchar(32) NOT NULL DEFAULT 'NULL' COMMENT '昵称',
                            `password` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'NULL' COMMENT '密码',
                            `status` tinyint(1) DEFAULT '0' COMMENT '用户状态（0正常 1停用）',
                            `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
                            `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
                            `sex` tinyint(1) DEFAULT NULL COMMENT '用户性别（0男，1女，2未知）',
                            `avatar` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像地址',
                            `create_time` date DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '最后一次修改时间',
                            `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表未删除，1代表已删除）',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';


INSERT INTO `sys_user` VALUES (1001, 'root', '周杰伦', '$2a$10$HebtQPbLFf3YrO6B1n8Sb.AWHAz8SZtAc48IFGm8iSXjZsym3GPii', 0, '1550324080@qq.com', '18420163207', 0, 'https://img2.baidu.com/it/u=670341883,3643142939&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '2022-09-26 23:46:02', '2022-09-26 23:46:05', 0);
INSERT INTO `sys_user` VALUES (1002, 'test', '蔡徐坤', '$2a$10$HebtQPbLFf3YrO6B1n8Sb.AWHAz8SZtAc48IFGm8iSXjZsym3GPii', 0, '1550324080@qq.com', '18420172020', 0, 'https://img2.baidu.com/it/u=361550957,796293689&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '2022-09-26 23:46:02', '2022-09-26 23:46:05', 0);

INSERT INTO `sys_user` VALUES (1003, 'root1', '周杰伦1', '$2a$10$HebtQPbLFf3YrO6B1n8Sb.AWHAz8SZtAc48IFGm8iSXjZsym3GPii', 0, '1550324080@qq.com', '18420193012', 0, 'https://img2.baidu.com/it/u=670341883,3643142939&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500', '2022-09-26 23:46:02', '2022-09-26 23:46:05', 0);

/* 角色表 */
DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
                            `id` bigint(20) NOT NULL COMMENT '主键',
                            `name` varchar(64) DEFAULT NULL COMMENT '角色权限名称，比如管理员',
                            `role_key` varchar(64) DEFAULT NULL COMMENT '角色权限关键字，比如admin',
                            `status` tinyint(1) DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
                            `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表未删除，1代表已删除）',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '最后一次修改时间',
                            `remark` varchar(256) DEFAULT NULL COMMENT '备注',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';


INSERT INTO sys_role VALUES (2001,'超级管理员','admin',0,0,'2022-09-26 23:46:02','2022-09-28 23:46:02','超级管理员角色');
INSERT INTO sys_role VALUES (2002,'普通角色','user',0,0,'2022-09-25 10:23:02','2022-09-28 10:33:02','普通角色');
INSERT INTO sys_role VALUES (2003,'黑名单角色','blackListUser',0,0,'2022-09-25 10:23:02','2022-09-28 10:33:02','黑名单角色');
INSERT INTO sys_role VALUES (2004,'测试角色1','testRole1',0,0,'2022-09-25 10:23:02','2022-09-28 10:33:02','测试角色1');

INSERT INTO sys_role VALUES (2005,'测试角色2','testRole2',0,0,'2022-09-25 10:23:02','2022-09-28 10:33:02','测试角色2');
INSERT INTO sys_role VALUES (2006,'测试角色3','testRole3',0,0,'2022-09-25 10:23:02','2022-09-28 10:33:02','测试角色3');
INSERT INTO sys_role VALUES (2007,'测试角色4','testRole4',0,0,'2022-09-25 10:23:02','2022-09-28 10:33:02','测试角色4');
INSERT INTO sys_role VALUES (2008,'测试角色5','testRole5',0,0,'2022-09-25 10:23:02','2022-09-28 10:33:02','测试角色5');
INSERT INTO sys_role VALUES (2009,'测试角色6','testRole6',0,0,'2022-09-25 10:23:02','2022-09-28 10:33:02','测试角色6');
INSERT INTO sys_role VALUES (2010,'测试角色7','testRole7',0,0,'2022-09-25 10:23:02','2022-09-28 10:33:02','测试角色7');
INSERT INTO sys_role VALUES (2011,'测试角色8','testRole8',0,0,'2022-09-25 10:23:02','2022-09-28 10:33:02','测试角色8');
INSERT INTO sys_role VALUES (1581238859201536002,'测试角色9','testRole9',0,0,'2022-09-25 10:23:02','2022-09-28 10:33:02','测试角色9');
INSERT INTO sys_role VALUES (1581238859201581195,'测试角色10','testRole10',0,0,'2022-09-25 10:23:02','2022-09-28 10:33:02','测试角色10');


/* 菜单表。也就是后台侧边栏菜单（本质上其实也是接口菜单权限）与接口菜单权限的表 */
DROP TABLE IF EXISTS `sys_menu`;

CREATE TABLE `sys_menu` (
                            `id` bigint(20) NOT NULL COMMENT '主键',
                            `parent_id` bigint(20) DEFAULT '0' COMMENT '后台侧边栏。父菜单ID,一级菜单为0',
                            `menu_name` varchar(64) NOT NULL DEFAULT 'NULL' COMMENT '菜单/权限名称',
                            `path` varchar(200) DEFAULT NULL COMMENT 'vue路由地址（type=1才会生效，type=0和2不生效）',
                            `component` varchar(128) DEFAULT NULL COMMENT '动态路由要用到。views目录下的组件名,自动会补上前缀‘../views’，这个前缀是固定的写法不能写到数据库里不然会报错（type=1才会生效，type=0和2不生效）',
                            `status` tinyint(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
                            `visible` tinyint(1) DEFAULT '0' COMMENT '菜单显示状态（0显示 1隐藏）（type=0或者1才会生效，type=2不生效）',
                            `perms` varchar(100) DEFAULT NULL COMMENT '菜单权限标识，比如sys:user:list(type=0设置为null即可，不会生效)',
                            `type` int NOT NULL COMMENT '菜单类型。0：目录（点击后台侧边栏可以展开成下一级菜单的按钮）;1：菜单（点击后台侧边栏直接跳转vue路由组件的按钮）;2：按钮;菜单里面的按钮',
                            `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标（type=0或者1才会生效，type=2不生效）',
                            `create_time` datetime DEFAULT NULL COMMENT '创建时间',
                            `update_time` datetime DEFAULT NULL COMMENT '最后一次修改时间',
                            `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表未删除，1代表已删除）',
                            `sort` int DEFAULT '1' COMMENT '前端菜单排序，默认值为1，1的优先级最高，排在最上面',
                            `remark` varchar(256) DEFAULT NULL COMMENT '备注',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单表';


/* 系统管理目录 */
INSERT INTO sys_menu VALUES (3001,0,'系统管理',null,null,0,0,null,0,'el-icon-eleme','2022-09-26 23:46:02','2022-09-28 23:46:02',0,1,'bz');

/* 用户管理菜单 */
INSERT INTO sys_menu VALUES (3002,3001,'用户管理','/user/list','/user-list/index',0,0,'sys:user:list',1,'el-icon-s-order','2022-09-26 23:46:02','2022-09-28 23:46:02',0,2,'bz');

/* 用户管理里面的按钮权限 */
INSERT INTO sys_menu VALUES (3003,3002,'新增用户',null,null,0,0,'sys:user:list:add',2,null,'2022-09-26 23:46:02','2022-09-28 23:46:02',0,3,'bz');
INSERT INTO sys_menu VALUES (3004,3002,'修改用户',null,null,0,0,'sys:user:list:update',2,null,'2022-09-26 23:46:02','2022-09-28 23:46:02',0,4,'bz');
INSERT INTO sys_menu VALUES (3005,3002,'删除用户',null,null,0,0,'sys:user:list:delete',2,null,'2022-09-26 23:46:02','2022-09-28 23:46:02',0,5,'bz');
INSERT INTO sys_menu VALUES (3020,3002,'分配角色',null,null,0,0,'sys:user:list:assign-role',2,null,'2022-09-26 23:46:02','2022-09-28 23:46:02',0,5,'bz');


/* 角色管理菜单 */
INSERT INTO sys_menu VALUES (3006,3001,'角色管理','/role/list','/role-list/index',0,0,'sys:role:list',1,'el-icon-suitcase','2022-09-26 23:46:02','2022-09-28 23:46:02',0,6,'bz');
/* 角色管理里面的按钮权限 */
INSERT INTO sys_menu VALUES (3007,3006,'新增角色',null,null,0,0,'sys:role:list:add',2,null,'2022-09-26 23:46:02','2022-09-28 23:46:02',0,7,'bz');
INSERT INTO sys_menu VALUES (3008,3006,'修改角色',null,null,0,0,'sys:role:list:update',2,null,'2022-09-26 23:46:02','2022-09-28 23:46:02',0,8,'bz');
INSERT INTO sys_menu VALUES (3009,3006,'删除角色',null,null,0,0,'sys:role:list:delete',2,null,'2022-09-26 23:46:02','2022-09-28 23:46:02',0,9,'bz');
INSERT INTO sys_menu VALUES (3021,3006,'分配菜单',null,null,0,0,'sys:role:list:assign-menu',2,null,'2022-09-26 23:46:02','2022-09-28 23:46:02',0,9,'bz');


/* 菜单（权限）管理 */
INSERT INTO sys_menu VALUES (3010,3001,'菜单管理','/menu/list','/menu-list/index',0,0,'sys:menu:list',1,'el-icon-s-custom','2022-09-26 23:46:02','2022-09-28 23:46:02',0,10,'bz');

/* 菜单管理里面的按钮权限 */
INSERT INTO sys_menu VALUES (3011,3010,'新增菜单',null,null,0,0,'sys:menu:list:add',2,null,'2022-09-26 23:46:02','2022-09-28 23:46:02',0,11,'bz');
INSERT INTO sys_menu VALUES (3012,3010,'修改菜单',null,null,0,0,'sys:menu:list:update',2,null,'2022-09-26 23:46:02','2022-09-28 23:46:02',0,12,'bz');
INSERT INTO sys_menu VALUES (3013,3010,'删除菜单',null,null,0,0,'sys:menu:list:delete',2,null,'2022-09-26 23:46:02','2022-09-28 23:46:02',0,13,'bz');


-- 菜单扩展部分

/* 登录日志菜单 */
INSERT INTO sys_menu VALUES (3014,3001,'登录日志','/log/login/list','/login-log/index',0,0,'sys:log:login',1,'el-icon-suitcase','2022-09-26 23:46:02','2022-09-28 23:46:02',0,14,'bz');

/* 登录日志菜单里面的删除日志权限 */
INSERT INTO sys_menu VALUES (3015,3014,'删除登录日志',null,null,0,0,'sys:log:login:delete',2,null,'2022-09-26 23:46:02','2022-09-28 23:46:02',0,15,'bz');


/* 操作日志菜单 */
INSERT INTO sys_menu VALUES (3016,3001,'操作日志','/log/operation/list','/operation-log/index',0,0,'sys:log:operation',1,'el-icon-suitcase','2022-09-26 23:46:02','2022-09-28 23:46:02',0,16,'bz');

/* 操作日志菜单里面的删除日志权限 */
INSERT INTO sys_menu VALUES (3017,3016,'删除操作日志',null,null,0,0,'sys:log:operation:delete',2,null,'2022-09-26 23:46:02','2022-09-28 23:46:02',0,17,'bz');

/* SQL监控菜单 */
INSERT INTO sys_menu VALUES (3018,3001,'SQL监控','/monitor/sql','/monitor-sql/index',0,0,'sys:monitor:sql',1,'el-icon-suitcase','2022-09-26 23:46:02','2022-09-28 23:46:02',0,18,'bz');

/* 服务器监控菜单 */
INSERT INTO sys_menu VALUES (3019,3001,'服务器监控','/monitor/server','/monitor-server/index',0,0,'sys:monitor:server',1,'el-icon-suitcase','2022-09-26 23:46:02','2022-09-28 23:46:02',0,19,'bz');




/* 三级目录 */
INSERT INTO sys_menu VALUES (3050,0,'一级菜单',null,null,0,0,null,0,'el-icon-s-tools','2022-09-26 23:46:02','2022-09-28 23:46:02',0,24,'bz');
INSERT INTO sys_menu VALUES (3051,3050,'二级菜单',null,null,0,0,null,0,'el-icon-eleme','2022-09-26 23:46:02','2022-09-28 23:46:02',0,25,'bz');
INSERT INTO sys_menu VALUES (3052,3051,'三级菜单1','/page1','/test-page/page1',0,0,'sys:third1',1,'el-icon-suitcase','2022-09-26 23:46:02','2022-09-28 23:46:02',0,26,'bz');
INSERT INTO sys_menu VALUES (3053,3051,'三级菜单2','/page2','/test-page/page2',0,0,'sys:third2',1,'el-icon-tickets','2022-09-26 23:46:02','2022-09-28 23:46:02',0,27,'bz');
INSERT INTO sys_menu VALUES (3054,3051,'三级菜单3','/page3','/test-page/page3',0,0,'sys:third3',1,'el-icon-tickets','2022-09-26 23:46:02','2022-09-28 23:46:02',0,28,'bz');




/* 用户-角色关联表 */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
                                 `id` bigint(20) NOT NULL COMMENT '主键',
                                 `user_id` bigint(20) NOT NULL COMMENT '用户id',
                                 `role_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '角色id',
                                 PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-角色表';

INSERT INTO sys_user_role VALUES (4001,1001,2001);
INSERT INTO sys_user_role VALUES (4002,1001,2002);
INSERT INTO sys_user_role VALUES (4003,1001,2007);
INSERT INTO sys_user_role VALUES (4004,1001,1581238859201536002);


INSERT INTO sys_user_role VALUES (4005,1002,2002);



INSERT INTO sys_user_role VALUES (4006,1003,2003);


/* 角色-菜单关联表 */

DROP TABLE IF EXISTS `sys_role_menu`;

CREATE TABLE `sys_role_menu` (
                                 `id` bigint(20) NOT NULL COMMENT '主键',
                                 `role_id` bigint(20) NOT NULL COMMENT '角色ID',
                                 `menu_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '菜单id',
                                 PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色-菜单表';

-- 管理员菜单(即使是目录也要把这个目录记录添加到sys_role_menu表中，否则展示不出来）
INSERT INTO sys_role_menu VALUES (5001,2001,3001);
INSERT INTO sys_role_menu VALUES (5002,2001,3002);
INSERT INTO sys_role_menu VALUES (5003,2001,3003);
INSERT INTO sys_role_menu VALUES (5004,2001,3004);
INSERT INTO sys_role_menu VALUES (5005,2001,3005);
INSERT INTO sys_role_menu VALUES (5006,2001,3006);
INSERT INTO sys_role_menu VALUES (5007,2001,3007);
INSERT INTO sys_role_menu VALUES (5008,2001,3008);
INSERT INTO sys_role_menu VALUES (5009,2001,3009);
INSERT INTO sys_role_menu VALUES (5010,2001,3010);
INSERT INTO sys_role_menu VALUES (5011,2001,3011);
INSERT INTO sys_role_menu VALUES (5012,2001,3012);
INSERT INTO sys_role_menu VALUES (5013,2001,3013);
INSERT INTO sys_role_menu VALUES (5014,2001,3014);
INSERT INTO sys_role_menu VALUES (5015,2001,3015);
INSERT INTO sys_role_menu VALUES (5016,2001,3016);
INSERT INTO sys_role_menu VALUES (5017,2001,3017);
INSERT INTO sys_role_menu VALUES (5018,2001,3018);
INSERT INTO sys_role_menu VALUES (5019,2001,3019);
INSERT INTO sys_role_menu VALUES (5020,2001,3020);
INSERT INTO sys_role_menu VALUES (5021,2001,3021);



-- 多级菜单权限
INSERT INTO sys_role_menu VALUES (5050,2001,3050);
INSERT INTO sys_role_menu VALUES (5051,2001,3051);
INSERT INTO sys_role_menu VALUES (5052,2001,3052);
INSERT INTO sys_role_menu VALUES (5053,2001,3053);
INSERT INTO sys_role_menu VALUES (5054,2001,3054);



-- 普通用户菜单(即使是目录也要把这个目录记录添加到sys_role_menu表中，否则展示不出来）
INSERT INTO sys_role_menu VALUES (5059,2002,3001);
INSERT INTO sys_role_menu VALUES (5060,2002,3002);
INSERT INTO sys_role_menu VALUES (5061,2002,3006);
INSERT INTO sys_role_menu VALUES (5062,2002,3010);
INSERT INTO sys_role_menu VALUES (5063,2002,3019);
INSERT INTO sys_role_menu VALUES (5064,2002,3020);



INSERT INTO sys_role_menu VALUES (5064,2002,3004);


/* 菜单权限扩展部分 */


-- 多级菜单权限
INSERT INTO sys_role_menu VALUES (5099,2002,3050);
INSERT INTO sys_role_menu VALUES (5100,2002,3051);
INSERT INTO sys_role_menu VALUES (5101,2002,3052);



-- 登录日志表

DROP TABLE IF EXISTS `sys_login_log`;

CREATE TABLE `sys_login_log`  (
                                  `id` bigint(20) NOT NULL COMMENT '主键',
                                  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录用户的用户名',
                                  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录用户的ip',
                                  `address` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录用户的ip所在地',
                                  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录用户使用的浏览器',
                                  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录用户使用的操作系统',
                                  `login_time` datetime NOT NULL COMMENT '登录时间',
                                  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表未删除，1代表已删除）',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='登录日志表';

-- 操作日志表

DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
                                 `id` bigint(20) NOT NULL COMMENT '主键',
                                 `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行操作的用户名',
                                 `type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '操作类型',
                                 `uri` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '访问的接口uri',
                                 `time` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '访问接口耗时（毫秒）',
                                 `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行操作的用户的ip',
                                 `address` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行操作的用户的ip对应的地址',
                                 `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行操作的用户所使用的浏览器',
                                 `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '执行操作的用户所使用的操作系统',
                                 `oper_time` datetime NOT NULL COMMENT '操作时间',
                                 `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志（0代表未删除，1代表已删除）',
                                 PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志表';






