package com.petal.system;

import com.petal.common.security.annotation.EnablePetalOauth2ResourceServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnablePetalOauth2ResourceServer
public class PetalSystemApplication {

    public static void main(String[] args) {

        SpringApplication.run(PetalSystemApplication.class,args);

    }

}
