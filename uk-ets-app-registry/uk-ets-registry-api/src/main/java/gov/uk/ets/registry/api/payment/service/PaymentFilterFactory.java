package gov.uk.ets.registry.api.payment.service;

import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.payment.domain.PaymentFilter;
import gov.uk.ets.registry.api.payment.web.model.PaymentSearchCriteria;
import gov.uk.ets.registry.api.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
@Log4j2
public class PaymentFilterFactory {

    private final AuthorizationService authorizationService;
    
    
    /**
     * Creates a {@link PaymentFilter} value object.
     *
     * @param criteria The {@link PaymentSearchCriteria} criteria
     * @return The produced {@link PaymentFilter} value object
     */
    public PaymentFilter createPaymentFilter(PaymentSearchCriteria criteria) {

        boolean isAdmin = authorizationService.hasClientRole(UserRole.SENIOR_REGISTRY_ADMINISTRATOR) ||
        		authorizationService.hasClientRole(UserRole.JUNIOR_REGISTRY_ADMINISTRATOR) ||
        		authorizationService.hasClientRole(UserRole.READONLY_ADMINISTRATOR);
        String userURID = null;
        
        if (!isAdmin) {
            userURID = authorizationService.getClaim(OAuth2ClaimNames.URID);
        }

        return PaymentFilter.builder()
            .referenceNumber(criteria.getReferenceNumber())
            .adminSearch(isAdmin)
            .urid(userURID)
            .build();
    }    
}
