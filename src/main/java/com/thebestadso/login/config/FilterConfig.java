package com.thebestadso.login.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thebestadso.login.filter.JwtValidationFilter;

@Configuration // Fábrica de beans
public class FilterConfig {
    @Bean
    FilterRegistrationBean<JwtValidationFilter> jwtFilter(JwtValidationFilter jwtValidationFilter) {
        FilterRegistrationBean<JwtValidationFilter> registrationBean = new FilterRegistrationBean<>();

        // Filtro que vamos a utilizar
        registrationBean.setFilter(jwtValidationFilter);

        // A qué rutas se aplica el filtro (todas)
        registrationBean.addUrlPatterns("/*");

        // Orden de prioridad del filtro
        registrationBean.setOrder(1);

        return registrationBean;
    }
}
