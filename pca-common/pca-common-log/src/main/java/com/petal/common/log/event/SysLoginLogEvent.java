package com.petal.common.log.event;

import com.petal.common.base.entity.SysLoginLog;
import org.springframework.context.ApplicationEvent;

/**
 * 登录日志事件实体类
 *
 * @author youzhengjie
 * @date 2023/06/11 18:12:09
 */
public class SysLoginLogEvent extends ApplicationEvent {

	public SysLoginLogEvent(SysLoginLog sysLoginLog) {
		super(sysLoginLog);
	}

}
