package com.petal.system.aop;

import com.petal.common.base.annotation.OperLog;
import com.petal.common.base.entity.SysOperationLog;
import com.petal.common.base.utils.BrowserUtil;
import com.petal.common.base.utils.IpToAddressUtil;
import com.petal.common.base.utils.IpUtil;
import com.petal.common.base.utils.SnowId;
import com.petal.common.security.service.SecurityOauth2User;
import com.petal.system.service.SysOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 操作日志切面类，说白了就是为了触发注解
 *
 * @author youzhengjie
 * @date 2022/10/22 22:44:19
 */
@Aspect
@Component
@Slf4j
public class OperLogAspect {

    private SysOperationLogService sysOperationLogService;

    @Autowired
    public void setSysOperationLogService(SysOperationLogService sysOperationLogService) {
        this.sysOperationLogService = sysOperationLogService;
    }

    //OperationLog注解的aop切面切入点
    @Pointcut("@annotation(com.petal.common.base.annotation.OperLog)")
    public void operationLogPointCut(){

    }


    /**
     * 触发操作日志注解
     *
     * @param proceedingJoinPoint 进行连接点
     * @return {@link Object}
     */
    //环绕通知
    @Around("operationLogPointCut()")
    public Object operationLog(ProceedingJoinPoint proceedingJoinPoint){

        Object proceed=null;
        try {
            //获取调用方法前的毫秒值
            long startMs = System.currentTimeMillis();
            //正式执行被注解标记的方法
            proceed= proceedingJoinPoint.proceed();
            //等调用完proceedingJoinPoint.proceed()之后，说明接口方法已经执行了，就可以把OperationLog存到数据库中。
            ServletRequestAttributes servletRequestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            //获取HttpServletRequest
            HttpServletRequest request = servletRequestAttributes.getRequest();
            //获取正在访问的并且有OperationLog注解的接口方法的MethodSignature对象
            MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
            //获取正在访问的并且有OperationLog注解的接口方法的OperationLog注解
            OperLog operLogAnnotation = signature.getMethod().getAnnotation(OperLog.class);
            //拿到注解值
            String annotationValue = operLogAnnotation.value();
            //从springsecurity中获取当前访问的用户名（从这里就可以看出，只有被springsecurity拦截的接口才能获取到用户名）
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            SecurityOauth2User securityOauth2User = (SecurityOauth2User) authentication.getPrincipal();
            String userName = securityOauth2User.getUsername();
            //获取正在访问的并且有OperationLog注解的接口的uri
            String uri = request.getRequestURI();
            //获取ip
            String ipAddr = IpUtil.getIpAddrByHttpServletRequest(request);
            //获取ip所在的地址
            String address = IpToAddressUtil.getCityInfo(ipAddr);
            //获取用户使用的浏览器
            String browserName = BrowserUtil.getBrowserName(request);
            //获取用户使用的操作系统
            String osName = BrowserUtil.getOsName(request);
            //获取调用方法后+注解的一些操作的毫秒值
            long endMs = System.currentTimeMillis();
            //访问接口的耗时
            String timeMs=(endMs-startMs)+"ms";

            //插入OperationLog数据到数据库
            SysOperationLog sysOperationLog = SysOperationLog.builder()
                        .id(SnowId.nextId())
                        .username(userName)
                        .type(annotationValue)
                        .uri(uri)
                        .time(timeMs)
                        .ip(ipAddr)
                        .address(address)
                        .browser(browserName)
                        .os(osName)
                        .operTime(LocalDateTime.now())
                        .build();
            sysOperationLogService.save(sysOperationLog);

            // 插入OperationLog数据到ElasticSearch
            sysOperationLogService.addOperationLogToEs(sysOperationLog);

            return proceed;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
