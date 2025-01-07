package com.github.hbq969.gateway.manage.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * @author : hbq969@gmail.com
 * @description : 跨域配置
 * @createTime : 2023/7/21 08:50
 */
@Configuration
@Slf4j
public class CorsWebMvcConfigurer implements WebMvcConfigurer {

    private String[] allowedOrigins = new String[]{"*"};

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("允许的跨域域名: {}", Arrays.toString(allowedOrigins));
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowCredentials(false)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
