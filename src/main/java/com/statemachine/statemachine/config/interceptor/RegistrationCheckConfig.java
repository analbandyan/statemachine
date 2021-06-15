package com.statemachine.statemachine.config.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RegistrationCheckConfig implements WebMvcConfigurer {

    private final RegistrationCheckInterceptor registrationCheckInterceptor;

    public RegistrationCheckConfig(RegistrationCheckInterceptor registrationCheckInterceptor) {
        this.registrationCheckInterceptor = registrationCheckInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(registrationCheckInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/v1/gdpr-consent");
    }

}
