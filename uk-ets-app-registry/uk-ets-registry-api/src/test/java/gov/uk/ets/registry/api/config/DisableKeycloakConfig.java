package gov.uk.ets.registry.api.config;

import org.keycloak.representations.AccessToken;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.context.WebApplicationContext;

/**
 * Permits all requests and provides a dummy Keycloak access token.
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name="keycloak.enabled",havingValue="false")
public class DisableKeycloakConfig extends WebSecurityConfigurerAdapter {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/**").permitAll();
        http.headers().frameOptions().disable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(WebSecurity security) {
        security.ignoring().antMatchers("/**");
    }

    /**
     * {@inheritDoc}
     */
    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public AccessToken accessToken() {
        AccessToken token = new AccessToken();
        token.setSubject("uk");
        token.setName("Tom Sawyer");
        return token;
    }	
	
}
