package com.dbs.watcherservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

//    @Bean
//    public RequestInterceptor requestInterceptor() {
//        return template;
//                -> {
//            template.header("Content-Type", "application/json");
//            template.header("Authorization", "Bearer YOUR_TOKEN");
//        };
//    }
}