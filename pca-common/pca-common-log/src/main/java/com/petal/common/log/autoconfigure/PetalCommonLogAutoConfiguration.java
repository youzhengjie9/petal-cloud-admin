package com.petal.common.log.autoconfigure;

import com.petal.common.log.listener.SysLoginLogListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * feign自动配置类
 *
 * @author youzhengjie
 * @date 2023/05/10 11:15:05
 */
@Configuration
@ConditionalOnWebApplication
@Import({
        SysLoginLogListener.class
})
public class PetalCommonLogAutoConfiguration {

}
