package gov.uk.ets.registry.api.authz;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.test.context.support.WithSecurityContextFactory;



public class WithMockEtsUserSecurityContextFactory implements WithSecurityContextFactory<WithMockEtsUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockEtsUser user) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.addAll(Stream.of(user.roles()).map(r -> "ROLE_" + r).map(SimpleGrantedAuthority::new).map(GrantedAuthority.class::cast).toList());
        authorities.addAll(Stream.of(user.scopes()).map(r -> "SCOPE_" + r).map(SimpleGrantedAuthority::new).map(GrantedAuthority.class::cast).toList());
        Map<String, Object> attributes = Map.of("iss", "https://localhost/auth/realms/uk-ets",
                  "aud", "uk-ets-registry-api",
                  "sub", "7856bfc4-adce-4b1a-8e3a-2fbdf0409e79",
                  "typ", "Bearer",
                  "azp", "uk-ets-web-app",
                  "state",user.state(),
                  "urid", user.urid(),
                  "session_state", "39bd294e-7248-460f-80ed-53efce7f4f39");
        DefaultOAuth2AuthenticatedPrincipal principal = new DefaultOAuth2AuthenticatedPrincipal(attributes,authorities);
        OAuth2AccessToken credentials = new OAuth2AccessToken(TokenType.BEARER, "ue93ueu93", Instant.now().minusSeconds(120) , Instant.now().plusSeconds(300));
        Authentication auth = new BearerTokenAuthentication(principal,credentials,authorities);
        context.setAuthentication(auth);

        return context;
    }
}
