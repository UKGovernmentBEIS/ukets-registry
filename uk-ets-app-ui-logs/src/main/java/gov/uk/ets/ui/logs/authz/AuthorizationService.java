package gov.uk.ets.ui.logs.authz;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;

import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthorizationService {

    public String getClaim(OAuth2ClaimNames oauth2claim) {
        return getPrincipal().getAttribute(oauth2claim.getClaimName());
    }
    
    private DefaultOAuth2AuthenticatedPrincipal getPrincipal() {
        Authentication authentication = getAuthentication();
        return DefaultOAuth2AuthenticatedPrincipal.class.cast(authentication.getPrincipal());
    }
    
    private BearerTokenAuthentication getAuthentication() {
        return BearerTokenAuthentication.class.cast(SecurityContextHolder.getContext().getAuthentication());
    }
}
