package gov.uk.ets.ui.logs.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import gov.uk.ets.lib.commons.security.oauth2.token.UkEtsOpaqueTokenIntrospector;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.util.JsonSerialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import uk.ets.lib.commons.auth.AuthorizationsLoader;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true")
@Log4j2
public class OAuth2ResourceServerSecurityConfiguration {

    @Value("${spring.security.oauth2.resourceserver.opaque-token.introspection-uri}")
    private String tokenIntrospectionUri;    
    @Value("classpath:keycloak/uk-ets-app-ui-logs-api-authz-config.json")
    private Resource apiAuthzConfig;
    @Value("${application.url}")
    private String applicationUrl;
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    @Value("${keycloak.realm}")
    private String realm;    
    @Value("${keycloak.resource}")
    private String resource;     
    @Value("${keycloak.credentials.secret}")
    private String secret;
    /** The Keycloak policy enforcer.*/   
    private PolicyEnforcerConfig config;
        
    /**
     * Initializes the Keycloak policy enforcer config by reading the policy-enforcer.json file.
     */
    @PostConstruct
    public void populatePolicyEnforcerConfigCache() {
        if (Objects.isNull(config)) {
            try {
                config = JsonSerialization.readValue(new ClassPathResource("keycloak/policy-enforcer.json").getInputStream(), PolicyEnforcerConfig.class);
                config.setAuthServerUrl(authServerUrl);
                config.setCredentials(Map.ofEntries(Map.entry(CredentialRepresentation.SECRET,secret)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }          
        
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize
              .requestMatchers(HttpMethod.GET,"/api-ui-logs/actuator/health","/v3/api-docs").permitAll()
              .requestMatchers("/api-ui-logs/**").hasAnyRole("ets_user")        
              .anyRequest()
              .authenticated())
              .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                                                   .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy()))
              .csrf(csrf -> csrf.disable())              
              .cors(cors -> cors.configurationSource(corsConfigurationSource()))            
              .oauth2ResourceServer(server -> server.opaqueToken(Customizer.withDefaults())) 
              .addFilterAfter(createPolicyEnforcerFilter(), BearerTokenAuthenticationFilter.class)
              .headers(headers -> headers
                  .contentSecurityPolicy(csp -> csp.policyDirectives(SecurityHeaders.CSP_HEADER))
                  .httpStrictTransportSecurity(Customizer.withDefaults())
                  .referrerPolicy(Customizer.withDefaults())
                  .addHeaderWriter(new StaticHeadersWriter("Expect-CT", "max-age=86400, enforce"))
                  .addHeaderWriter(new StaticHeadersWriter("X-Permitted-Cross-Domain-Policies", "none"))  
                  // without this there is an error concerning the cookies iframe (specifically this header is set to DENY)
                  .frameOptions(FrameOptionsConfig::sameOrigin)          
                  .permissionsPolicy(p -> p.policy(SecurityHeaders.FEATURE_POLICY_HEADER))
            );

        return http.build();
    }
    
    @Bean
    public OpaqueTokenIntrospector introspector() {
        return new UkEtsOpaqueTokenIntrospector(tokenIntrospectionUri,resource, secret);
    }     

    private ServletPolicyEnforcerFilter createPolicyEnforcerFilter() {
        return new ServletPolicyEnforcerFilter((request) -> config);
    }   
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(applicationUrl));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("WWW-Authenticate"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }    
    
    @Bean
    public AuthorizationsLoader authorizationsLoader(ObjectMapper mapper) {
        return new AuthorizationsLoader(mapper);
    }
    
    /**
     * Loads the Authorization configuration to Keycloak on service startup.
     * 
     * @param properties the Keycloak properties
     * @param mapper the object mapper
     * @return the command to run
     */
    @Profile("!integrationTest")
    @Bean
    public CommandLineRunner keycloakAuthorizationDataLoader(ObjectMapper mapper) {
        return new CommandLineRunner() {
            @Autowired
            ApplicationContext context;

            final Header header = new BasicHeader(HttpHeaders.USER_AGENT, "ui-logs-initial-security-config");
            final List<Header> headers = Lists.newArrayList(header);
            final HttpClient client = HttpClients.custom().setDefaultHeaders(headers).build();

            final org.keycloak.authorization.client.Configuration config =
                new org.keycloak.authorization.client.Configuration(
                    authServerUrl,
                    realm,
                    resource,
                    Map.of("secret",secret),
                    client);

            @Override
            public void run(String... args) {
                try {
                    authorizationsLoader(mapper).doLoad(config, apiAuthzConfig);
                } catch (Exception e) {
                    log.info("Could not perform operation, reason", e);
                    log.warn("Application will shutdown");
                    // If the authorization data loader fails we do not want the application to start
                    // since it will probably be unusable
                    int exitCode = SpringApplication.exit(context);
                    System.exit(exitCode);
                }
            }
        };
    }    
}
