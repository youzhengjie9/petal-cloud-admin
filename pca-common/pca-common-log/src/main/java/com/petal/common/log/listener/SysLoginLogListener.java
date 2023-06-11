package com.petal.common.log.listener;


import com.petal.common.base.entity.SysLoginLog;
import com.petal.common.log.event.SysLoginLogEvent;
import com.petal.common.openfeign.feign.SysLoginLogFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author lengleng 异步监听日志事件
 */
@Component
public class SysLoginLogListener {

	private SysLoginLogFeign sysLoginLogFeign;

	private HttpServletRequest request;

	private static final ThreadPoolExecutor threadPoolExecutor =
			new ThreadPoolExecutor(10,15,
					2L, TimeUnit.SECONDS,
					new LinkedBlockingQueue<>(), Executors.defaultThreadFactory(),
					new ThreadPoolExecutor.AbortPolicy());

	@Autowired
	public void setSysLoginLogFeign(SysLoginLogFeign sysLoginLogFeign) {
		this.sysLoginLogFeign = sysLoginLogFeign;
	}

	@Autowired
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@EventListener(SysLoginLogEvent.class)
	public void saveSysLog(SysLoginLogEvent sysLoginLogEvent) {
		//线程池异步调用
		threadPoolExecutor.execute(() ->{
			String username = (String) sysLoginLogEvent.getSource();
			sysLoginLogFeign.addLoginLog(username,request,"12345");
		});
	}

}
