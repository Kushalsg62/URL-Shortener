package com.kushalsg.urlshortener.config;

import com.kushalsg.urlshortener.web.TurnstileInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TurnstileInterceptor turnstileInterceptor;

    public WebMvcConfig(TurnstileInterceptor turnstileInterceptor) {
        this.turnstileInterceptor = turnstileInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(turnstileInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/verify",
                        "/verify/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/webjars/**",
                        "/error",
                        "/favicon.ico"
                );
    }
}