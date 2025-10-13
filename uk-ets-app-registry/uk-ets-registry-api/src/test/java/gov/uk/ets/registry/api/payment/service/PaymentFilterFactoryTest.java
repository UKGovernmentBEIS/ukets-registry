package gov.uk.ets.registry.api.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.payment.domain.PaymentFilter;
import gov.uk.ets.registry.api.payment.web.model.PaymentSearchCriteria;
import gov.uk.ets.registry.api.user.domain.UserRole;

import java.text.ParseException;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class PaymentFilterFactoryTest {

    @Mock
    PaymentSearchCriteria criteria;

    @Mock
    private AuthorizationService authorizationService;

    PaymentFilterFactory paymentFilterFactory;

    ExpectedFilterProperties expectedFilterProperties;
    
    @BeforeEach
    public void setup() throws ParseException {
    	paymentFilterFactory = new PaymentFilterFactory(authorizationService);

        expectedFilterProperties = ExpectedFilterProperties
                .builder()
                .referenceNumber(17272L)
                .build();
    } 
    
    
    @Test
    public void testCriteriaToFilterMapping() throws ParseException {
        mockCriteria();
        PaymentFilter filter = paymentFilterFactory.createPaymentFilter(criteria);
        assertEquals(expectedFilterProperties.referenceNumber, filter.getReferenceNumber());
    }
    
    
    @Test
    public void createTransactionFilterForAdmins() {
        // given
        given(authorizationService.hasClientRole(UserRole.SENIOR_REGISTRY_ADMINISTRATOR)).willReturn(true);
        // when
        PaymentFilter filter = paymentFilterFactory.createPaymentFilter(criteria);
        // then
        assertNull(filter.getUrid());
        assertTrue(filter.getAdminSearch());
    }

    @Test
    public void createTransactionFilterForNonAdmins() {
        // given
        given(authorizationService.getClaim(OAuth2ClaimNames.URID)).willReturn("a-urid-for-test");
        given(authorizationService.hasClientRole(UserRole.SENIOR_REGISTRY_ADMINISTRATOR)).willReturn(false);

        // when
        PaymentFilter filter = paymentFilterFactory.createPaymentFilter(criteria);

        // then
        assertEquals(filter.getUrid(), "a-urid-for-test");
        assertFalse(filter.getAdminSearch());
    }
    
    private void mockCriteria() {
        when(criteria.getReferenceNumber()).thenReturn(expectedFilterProperties.referenceNumber);
    }

    @Builder
    private static class ExpectedFilterProperties {
        private Long referenceNumber;
    }
    
}
