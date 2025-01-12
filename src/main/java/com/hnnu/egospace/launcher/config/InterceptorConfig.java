package com.hnnu.egospace.launcher.config;

import com.hnnu.egospace.launcher.common.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Resource
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/backend/**/login",
                    "/backend/**/register",
                    "/media/**",
                    "/feedback/submit",
                    "/error",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                );
    }
}