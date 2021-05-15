package at.cosylab.cloud.tnta.config.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterRegistrationConfig {

    private final Logger log = LoggerFactory.getLogger(WebAppCORSFilter.class);


    @Bean
    public FilterRegistrationBean webCORSFilterRegistration() {


        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(webAppCORSFilter());
        registration.addUrlPatterns("/tnta/cloud/*");
        registration.addUrlPatterns("/tnta/fog/*");
        registration.addUrlPatterns("/tnta/fog/certificate/revoke/*");
        registration.setName("webAppCORSFilter");
        registration.setOrder(1);
        return registration;
    }

    @Bean(name = "WebAppCORSFilter")
    public WebAppCORSFilter webAppCORSFilter() {
        return new WebAppCORSFilter();
    }
}
