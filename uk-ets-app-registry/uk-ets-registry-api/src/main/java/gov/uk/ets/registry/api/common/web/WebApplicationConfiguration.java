package gov.uk.ets.registry.api.common.web;

import gov.uk.ets.commons.logging.Config;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputStore;
import gov.uk.ets.registry.api.common.web.filter.CustomLoggingFilter;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web application related configuration. Note that the configuration relates only to web elements
 * of the application.
 */
@Configuration
@ServletComponentScan
@ComponentScan(basePackages = {"gov.uk.ets.commons.logging,gov.uk.ets.file.upload.services"})
public class WebApplicationConfiguration implements WebMvcConfigurer {

    /**
     * @return
     */
    @Bean
    @RequestScope
    public BusinessSecurityStore requestScopedBusinessSecurityStore() {
        return new BusinessSecurityStore();
    }

    /**
     * @return
     */
    @Bean
    @RequestScope
    public RuleInputStore requestScopedRuleInputStore() {
        return new RuleInputStore();
    }

    /**
     * Register the filter here, otherwise (using both @WebFilter and @Component) registers the filter twice.
     */
    @Bean
    public FilterRegistrationBean<CustomLoggingFilter> customLoggingFilter(AuthorizationService authorizationService,
                                                                           BuildProperties buildProperties,
                                                                           Config config) {
        CustomLoggingFilter filter = new CustomLoggingFilter();
        filter.setAuthorizationService(authorizationService);
        filter.setBuildProperties(buildProperties);
        filter.setConfig(config);
        FilterRegistrationBean<CustomLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/api-registry/*");
        return registrationBean;
    }
}
