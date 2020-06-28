package com.dchristofolli.projects.dataanalytics.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
    @Bean
    public String string() {
        return "";
    }

    @Bean
    public Integer integer(){
        return 0;
    }

    @Bean
    public Double aDouble(){
        return 0.0;
    }
}
