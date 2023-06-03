package com.petal.common.security.annotation;

import com.petal.common.security.resource.server.ResourceServerAutoConfiguration;
import com.petal.common.security.resource.server.ResourceServerConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import java.lang.annotation.*;


/**
 * 开启Oauth2资源服务器
 * <p>
 * @author youzhengjie
 * @date 2023/05/11 17:41:34
 */
@Documented
@Inherited
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import({ ResourceServerAutoConfiguration.class, ResourceServerConfiguration.class })
public @interface EnablePetalOauth2ResourceServer {



}
