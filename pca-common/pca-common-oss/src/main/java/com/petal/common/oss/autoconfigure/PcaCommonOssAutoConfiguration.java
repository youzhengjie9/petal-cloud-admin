package com.petal.common.oss.autoconfigure;

import com.petal.common.oss.config.SysMinioConfig;
import com.petal.common.oss.properties.SysMinioProperties;
import com.petal.common.oss.properties.SysQiniuOssProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({SysQiniuOssProperties.class, SysMinioProperties.class})
public class PcaCommonOssAutoConfiguration {

    @Bean
    public SysMinioConfig minioConfig(){
        return new SysMinioConfig();
    }

}
