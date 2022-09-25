package com.lin.srb.base.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket adminApiConfig() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("adminApi")
                .apiInfo(adminApiInfo())
                .select()
                //只显示admin路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }

    private ApiInfo adminApiInfo() {
        return new ApiInfoBuilder()
                .title("顺融宝后台管理系统-API文档")
                .description("本文档描述了顺融宝后台管理系统接口")
                .version("1.0")
                .contact(new Contact("gqlin", "http://lin.com", "1079535662@qq.com"))
                .build();
    }


    @Bean
    public Docket apiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("api")
                .apiInfo(apiInfo())
                .select()
                //只显示api路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("顺融宝-API文档")
                .description("本文档描述了顺融宝接口")
                .version("1.0")
                .contact(new Contact("gqlin", "http://lin.com", "1079535662@qq.com"))
                .build();
    }

    @Bean
    public Docket uaaConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("uaa")
                .apiInfo(uaaInfo())
                .select()
                //只显示api路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/uaa/.*")))
                .build();
    }
    private ApiInfo uaaInfo(){
        return new ApiInfoBuilder()
                .title("顺融宝-UAA文档")
                .description("本文档描述了顺融宝统一认证接口")
                .version("1.0")
                .contact(new Contact("gqlin", "http://lin.com", "1079535662@qq.com"))
                .build();
    }
}