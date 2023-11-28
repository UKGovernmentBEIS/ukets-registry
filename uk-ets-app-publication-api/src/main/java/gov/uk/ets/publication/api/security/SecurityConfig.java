package gov.uk.ets.publication.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticatedActionsFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakPreAuthActionsFilter;
import org.keycloak.adapters.springsecurity.filter.KeycloakSecurityContextRequestFilter;
import org.keycloak.adapters.springsecurity.management.HttpSessionManager;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import uk.ets.lib.commons.auth.AuthorizationsLoader;

@KeycloakConfiguration
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
@Log4j2
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

	@Value("classpath:keycloak/uk-ets-publication-api-authz-config.json")
    private Resource apiAuthzConfig;

    @Value("${application.url}")
    String applicationUrl;

    @Bean
    public AuthorizationsLoader authorizationsLoader(ObjectMapper mapper) {
        return new AuthorizationsLoader(mapper);
    }

    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(keycloakAuthenticationProvider());
    }

    /**
     * Defines the session authentication strategy.
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public FilterRegistrationBean keycloakAuthenticationProcessingFilterRegistrationBean(
        KeycloakAuthenticationProcessingFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean keycloakPreAuthActionsFilterRegistrationBean(
        KeycloakPreAuthActionsFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean keycloakAuthenticatedActionsFilterBean(
        KeycloakAuthenticatedActionsFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean keycloakSecurityContextRequestFilterBean(
        KeycloakSecurityContextRequestFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(HttpSessionManager.class)
    protected HttpSessionManager httpSessionManager() {
        return new HttpSessionManager();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.csrf().disable().authorizeRequests().antMatchers("/**").permitAll();
        http.cors(Customizer.withDefaults());
        http.headers()
            .contentSecurityPolicy(SecurityHeaders.CSP_HEADER)
            .and()
            .featurePolicy(SecurityHeaders.FEATURE_POLICY_HEADER)
            .and()
            .httpStrictTransportSecurity() // default is max-age=31536000 ; includeSubDomains
            .and()
            .referrerPolicy() // default is no-referrer
            .and()
            .addHeaderWriter(new StaticHeadersWriter("Expect-CT", "max-age=86400, enforce"))
            .addHeaderWriter(new StaticHeadersWriter("X-Permitted-Cross-Domain-Policies", "none"))
            .frameOptions()
            // without this there is an error concerning the cookies iframe (specifically this header is set to DENY)
            .sameOrigin()
        ;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(applicationUrl));
        configuration.setAllowedMethods(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @Primary
    public KeycloakConfigResolver keycloakConfigResolver() {
        return new UkEtsKeycloakSpringBootConfigResolver();
    }

    @Bean
    @SneakyThrows
    public ResourceServerRepresentation loadResourceServerRepresentation() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(apiAuthzConfig.getInputStream(), ResourceServerRepresentation.class);
    }

    @Profile("!integrationTest")
    @Bean
    public CommandLineRunner keycloakAuthorizationDataLoader(KeycloakSpringBootProperties properties,
                                                             ObjectMapper mapper) {
        return new CommandLineRunner() {
            @Autowired
            ApplicationContext context;

            final Header header = new BasicHeader(HttpHeaders.USER_AGENT, "ui-logs-initial-security-config");
            final List<Header> headers = Lists.newArrayList(header);
            final HttpClient client = HttpClients.custom().setDefaultHeaders(headers).build();

            final Configuration config =
                new Configuration(
                    properties.getAuthServerUrl(),
                    properties.getRealm(),
                    properties.getResource(),
                    properties.getCredentials(),
                    client);

            @Override
            public void run(String... args) {
                try {
                    authorizationsLoader(mapper).doLoad(config, apiAuthzConfig);
                } catch (Exception e) {
                    log.info("Could not perform operation, reason", e);
                    log.warn("Application will shutdown");
                    // If the authorisation data loader fails we do not want the application to start
                    // since it will probably be unusable
                    int exitCode = SpringApplication.exit(context);
                    System.exit(exitCode);
                }
            }
        };
    }
}
