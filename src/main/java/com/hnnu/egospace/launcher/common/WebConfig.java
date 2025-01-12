package com.hnnu.egospace.launcher.common;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(" ", clazz -> clazz.isAnnotationPresent(RestController.class));
    }
     
    @Resource
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // back-end and (login && register) token verification
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/api/**")
                  .excludePathPatterns("/api/*/admin/login")
                  .excludePathPatterns("/api/*/user/login")
                  .excludePathPatterns("/api/*/user/register");
                  
        
        // // front-end token verification
        // registry.addInterceptor(jwtInterceptor).addPathPatterns("/frontend/**")
        //           .excludePathPatterns("/frontend/*/home")
        //           .excludePathPatterns("/frontend/*/feedback");

    }

}

