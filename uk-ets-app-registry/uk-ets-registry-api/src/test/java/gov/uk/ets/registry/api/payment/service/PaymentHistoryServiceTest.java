package gov.uk.ets.registry.api.payment.service;

import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.payment.domain.PaymentHistory;
import gov.uk.ets.registry.api.payment.repository.PaymentHistoryRepository;
import gov.uk.ets.registry.api.payment.service.mapper.PaymentHistoryMapper;
import gov.uk.ets.registry.api.payment.web.model.PaymentHistoryCriteria;
import gov.uk.ets.registry.api.payment.web.model.PaymentHistoryDTO;
import gov.uk.ets.registry.api.payment.web.model.PaymentHistoryPropertyPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentHistoryServiceTest {

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    private PaymentHistoryMapper paymentHistoryMapper;

    @InjectMocks
    private PaymentHistoryService service;

    private PaymentHistory entity1;
    private PaymentHistory entity2;
    private PaymentHistoryDTO dto1;
    private PaymentHistoryDTO dto2;

    @BeforeEach
    void setUp() {
        entity1 = mock(PaymentHistory.class);
        entity2 = mock(PaymentHistory.class);
        dto1 = mock(PaymentHistoryDTO.class);
        dto2 = mock(PaymentHistoryDTO.class);
    }

    @Test
    void search_whenReferenceNumberIsNull_passesEmptyString() {

        PaymentHistoryCriteria criteria = new PaymentHistoryCriteria();
        criteria.setReferenceNumber(100L);

        PageParameters pageParameters = new PageParameters();
        pageParameters.setPage(0);
        pageParameters.setPageSize(10L);
        pageParameters.setSortField(PaymentHistoryPropertyPath.PAYMENT_HISTORY_ID);

        List<PaymentHistory> entities = List.of(entity1, entity2);
        Page<PaymentHistory> page = new PageImpl<>(entities);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        when(paymentHistoryRepository.findByReferenceNumberTextContaining("100", pageable))
                .thenReturn(page);
        List<PaymentHistoryDTO> expectedDtos = List.of(dto1, dto2);
        when(paymentHistoryMapper.toDto(entities)).thenReturn(expectedDtos);

        List<PaymentHistoryDTO> result = service.search(criteria, pageParameters);
        assertThat(result).isEqualTo(expectedDtos);
    }
}
