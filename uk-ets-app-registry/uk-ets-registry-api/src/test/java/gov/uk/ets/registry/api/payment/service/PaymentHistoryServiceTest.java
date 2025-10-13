package gov.uk.ets.registry.api.payment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.payment.domain.PaymentFilter;
import gov.uk.ets.registry.api.payment.repository.PaymentHistoryRepository;
import gov.uk.ets.registry.api.payment.shared.PaymentHistoryPropertyPath;
import gov.uk.ets.registry.api.payment.web.model.PaymentSearchCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@ExtendWith(MockitoExtension.class)
class PaymentHistoryServiceTest {

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;
    
    @Mock
    private PaymentFilterFactory paymentFilterFactory;
    
    @Mock
    private PaymentSearchCriteria criteria;
    
    @Mock
    private Pageable pageable;
    
    @Mock
    private PaymentFilter filter;

    @InjectMocks
    private PaymentHistoryService service;

    @Test
    void search() {
        // given
        given(paymentFilterFactory.createPaymentFilter(any())).willReturn(filter);
        // when
        PageParameters pageParameters = new PageParameters();
        pageParameters.setPage(0);
        pageParameters.setPageSize(10L);
        pageParameters.setSortField(PaymentHistoryPropertyPath.PAYMENT_ID);
        pageParameters.setSortDirection(Direction.ASC);
        service.search(criteria, pageParameters);
        // then
        then(paymentHistoryRepository).should(times(1)).search(filter, PageRequest.of(0,10 , Sort.by(Direction.ASC, PaymentHistoryPropertyPath.PAYMENT_ID)));
        then(paymentHistoryRepository).shouldHaveNoMoreInteractions();
    }
}
