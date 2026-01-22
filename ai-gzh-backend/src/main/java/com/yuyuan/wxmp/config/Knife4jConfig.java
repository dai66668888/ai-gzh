package com.yuyuan.wxmp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * *
 * Knife4j 配置
 *
 * @author cq
 * @since 2023/10/29
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI evaluationOpenApi() {
        Contact authorContact = new Contact()
                .name("鱼鸢网络");
        Info info = new Info()
                .title("微信公众号管理系统 API")
                .version("V1.0")
                .description("微信公众号管理系统接口文档")
                .contact(authorContact);
        return new OpenAPI().info(info);
    }
}
