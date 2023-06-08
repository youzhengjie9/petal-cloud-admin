package com.petal.common.oss.config;

import com.petal.common.oss.properties.SysMinioProperties;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * minio配置类
 *
 * @author youzhengjie
 * @date 2023-04-09 17:11:25
 */
@Configuration
public class SysMinioConfig {

    private SysMinioProperties sysMinioProperties;

    @Autowired
    public void setSysMinioProperties(SysMinioProperties sysMinioProperties) {
        this.sysMinioProperties = sysMinioProperties;
    }

    /**
     * minio-client bean
     *
     * @return {@link MinioClient}
     */
    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
                //配置minio的通信端点（url），和控制台端点不一样
                .endpoint(sysMinioProperties.getEndpoint())
                //配置minio的帐号密码
                .credentials(sysMinioProperties.getAccessKey(), sysMinioProperties.getSecretKey())
                .build();
    }

}
