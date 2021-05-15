package at.cosylab.cloud.acam.config;

import at.cosylab.cloud.acam.commons.filters.WebAppCORSFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterRegistrationConfig {

    private final Logger log = LoggerFactory.getLogger(WebAppCORSFilter.class);


    @Bean
    public FilterRegistrationBean mobileCORSFilterRegistration() {

        log.info("Registering cors filters!");


        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(webAppCORSFilter());
        registration.addUrlPatterns("/acam/auth/*");
        registration.addUrlPatterns("/acam/devicetype/*");
        registration.addUrlPatterns("/acam/account/*");
        registration.setName("webAppCORSFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean(name = "WebAppCORSFilter")
    public WebAppCORSFilter webAppCORSFilter() {
        return new WebAppCORSFilter();
    }
}
