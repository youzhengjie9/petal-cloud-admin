package com.petal.common.openfeign.autoconfigure;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * feign自动配置类
 *
 * @author youzhengjie
 * @date 2023/05/10 11:15:05
 */
@Configuration
@EnableFeignClients(basePackages = {
        "com.petal.common.openfeign.feign"
})
public class PetalCommonOpenFeignAutoConfiguration {


}
