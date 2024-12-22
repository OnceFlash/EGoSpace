package com.hnnu.egospace.launcher.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.pagehelper.PageInterceptor;

@Configuration
public class MyBatisPulsConfig {

    @Bean
    PageInterceptor pageInterceptor() {
        PageInterceptor paginationInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helpDialect", "mysql");
        paginationInterceptor.setProperties(properties);
        return paginationInterceptor;
    }
}
