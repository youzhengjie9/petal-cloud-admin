package com.petal.gateway;

import com.petal.common.swagger.autoconfigure.PetalCommonSwaggerAutoConfiguration;
import com.petal.gateway.properties.GatewayConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {PetalCommonSwaggerAutoConfiguration.class})
@EnableDiscoveryClient
@EnableConfigurationProperties(GatewayConfigProperties.class)
public class PetalGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetalGatewayApplication.class,args);

    }

}
