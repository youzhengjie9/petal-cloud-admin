package com.petal.common.swagger.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.petal.common.swagger.properties.SwaggerProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Response;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableOpenApi //注解启动用Swagger的使用，同时在配置类中对Swagger的通用参数进行配置
@EnableKnife4j //开启knife4j美化
public class SwaggerConfig implements EnvironmentAware {

    private String applicationName;

    /**
     * 文档版本
     */
    private static final String DOC_VERSION = "3.2" ;

    private SwaggerProperties swaggerProperties;

    @Autowired
    public void setSwaggerProperties(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public Docket createRestApi() {
        //文档概要信息
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                //是否开启 (true=开启,false=隐藏。生产环境建议设置为false)
                .enable(swaggerProperties.getEnable())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.any())
                .build()
                .globalResponses(HttpMethod.GET, getGlobalResponseMessage())
                .globalResponses(HttpMethod.POST, getGlobalResponseMessage());
    }


    /**
     * 生成接口信息，包括标题，联系人等
     *
     * @return {@link ApiInfo}
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(applicationName + "接口文档")
                .description(swaggerProperties.getDescription())
                // 本API负责人的联系信息
                .contact(
                        new Contact(
                            swaggerProperties.getContactName(),
                            swaggerProperties.getContactUrl(),
                            swaggerProperties.getContactEmail()
                        )
                )
                .version(DOC_VERSION)
                .build();
    }


    /**
     * 封装通用相应信息
     *
     * @return {@link List}<{@link Response}>
     */
    private List<Response> getGlobalResponseMessage() {
        List<Response> responseList = new ArrayList<>();
        responseList.add(new ResponseBuilder().code("404").description("未找到资源").build());
        return responseList;
    }

    /**
     * 增加如下配置可解决Spring Boot 2.6.x 与Swagger 3.0.0 不兼容问题
     *
     * @param webEndpointsSupplier        web端点供应商
     * @param servletEndpointsSupplier    servlet端点供应商
     * @param controllerEndpointsSupplier 控制器终端供应商
     * @param endpointMediaTypes          端点媒体类型
     * @param corsProperties              歌珥属性
     * @param webEndpointProperties       web端点属性
     * @param environment                 环境
     * @return {@link WebMvcEndpointHandlerMapping}
     */
    @Bean
    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(
            WebEndpointsSupplier webEndpointsSupplier, ServletEndpointsSupplier servletEndpointsSupplier,
            ControllerEndpointsSupplier controllerEndpointsSupplier, EndpointMediaTypes endpointMediaTypes,
            CorsEndpointProperties corsProperties, WebEndpointProperties webEndpointProperties, Environment environment) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping = webEndpointProperties.getDiscovery().isEnabled() &&
                (org.springframework.util.StringUtils.hasText(basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes, corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath), shouldRegisterLinksMapping, null);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.applicationName = environment.getProperty("spring.application.name");
    }

}