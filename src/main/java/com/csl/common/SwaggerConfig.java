package com.csl.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.csl.controller"))//扫描api路径，可以不写默认扫描全部controller
                .paths(PathSelectors.any())//表示路径选择器匹配所有路径
                .build();
    }

    private ApiInfo apiInfo() {//设置api文档信息
        return new ApiInfoBuilder()
                .title("秒杀项目")
                .description("秒杀项目接口文档")
                .version("1.0")
                .build();
    }
}
