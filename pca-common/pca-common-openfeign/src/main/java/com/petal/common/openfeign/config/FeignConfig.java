package com.petal.common.openfeign.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

//注意：此处配置@Configuration注解就会全局生效，如果想指定对应微服务生效，就不能配置
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }

//    /**
//     * 方法1：配置openFeign超时时间bean。
//     * 方法2：配置文件中加上下面属性配置
//     * ribbon:
//     *   # 连接超时时间
//     *   ConnectTimeout: 3000
//     *   ReadTimeout: 5000
//     */
//    @Bean
//    public Request.Options options(){
//        //连接超时时间。
//        final long CONNECT_TIMEOUT = 3;
//        //调用feign接口的方法的超时时间。例如下面READ_TIMEOUT=5，则说明stockFeignService.helloWorld()方法执行时间不能超过5秒，否则会报错。
//        final long READ_TIMEOUT = 5;
//        return new Request.Options(CONNECT_TIMEOUT, TimeUnit.SECONDS,
//                READ_TIMEOUT,TimeUnit.SECONDS,
//                true);
//    }

}
