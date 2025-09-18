package gov.uk.ets.registry.api.payment.service;

import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.PageableMapper;
import gov.uk.ets.registry.api.payment.domain.PaymentHistory;
import gov.uk.ets.registry.api.payment.repository.PaymentHistoryRepository;
import gov.uk.ets.registry.api.payment.service.mapper.PaymentHistoryMapper;
import gov.uk.ets.registry.api.payment.web.model.PaymentHistoryCriteria;
import gov.uk.ets.registry.api.payment.web.model.PaymentHistoryDTO;
import gov.uk.ets.registry.api.payment.web.model.PaymentHistorySearchPageableMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for payments history.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentHistoryMapper paymentHistoryMapper;

    public List<PaymentHistoryDTO> search(@Valid PaymentHistoryCriteria criteria, PageParameters pageParameters) {
        String param = criteria.getReferenceNumber() == null ? "" : String.valueOf(criteria.getReferenceNumber());

        PageableMapper pageableMapper = new PaymentHistorySearchPageableMapper();
        Pageable pageable = pageableMapper.get(pageParameters);

        Page<PaymentHistory> paymentHistories = paymentHistoryRepository
                .findByReferenceNumberTextContaining(param, pageable);

        return paymentHistoryMapper.toDto(paymentHistories.getContent());
    }

}
