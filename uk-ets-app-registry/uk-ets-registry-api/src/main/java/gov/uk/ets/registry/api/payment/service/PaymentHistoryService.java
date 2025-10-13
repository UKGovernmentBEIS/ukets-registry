package gov.uk.ets.registry.api.payment.service;

import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.PageableMapper;
import gov.uk.ets.registry.api.payment.domain.PaymentFilter;
import gov.uk.ets.registry.api.payment.domain.PaymentHistoryProjection;
import gov.uk.ets.registry.api.payment.repository.PaymentHistoryRepository;
import gov.uk.ets.registry.api.payment.web.model.PaymentHistorySearchPageableMapper;
import gov.uk.ets.registry.api.payment.web.model.PaymentSearchCriteria;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for payments history.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentFilterFactory paymentFilterFactory;

    public Page<PaymentHistoryProjection> search(@Valid PaymentSearchCriteria criteria, PageParameters pageParameters) {

        PageableMapper pageableMapper = new PaymentHistorySearchPageableMapper();
        Pageable pageable = pageableMapper.get(pageParameters);
        
        PaymentFilter filter = paymentFilterFactory.createPaymentFilter(criteria);
        
        return paymentHistoryRepository.search(filter, pageable);
    }

}
