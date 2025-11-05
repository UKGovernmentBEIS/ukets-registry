package gov.uk.ets.registry.api.payment.service;


import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.domain.PaymentHistory;
import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import gov.uk.ets.registry.api.payment.repository.PaymentHistoryRepository;
import gov.uk.ets.registry.api.payment.repository.PaymentRepository;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskCompleteResponse;
import gov.uk.ets.registry.api.payment.web.model.PaymentTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentTaskServiceTest {
    
    private static final Long REF_NUMBER = 12345L;

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;
    @Mock
    private PaymentTaskCompleteResponseFactory paymentTaskCompleteResponseFactory;

    @InjectMocks
    private PaymentTaskService paymentTaskService; // whatever your class name is

    @Test
    void shouldSetPaymentSuccessWhenApprovedBacs() {
        Payment payment = new Payment();
        payment.setReferenceNumber(REF_NUMBER);
        payment.setMethod(PaymentMethod.BACS);

        PaymentHistory history = new PaymentHistory();

        when(paymentRepository.findByReferenceNumber(REF_NUMBER))
                .thenReturn(Optional.of(payment));
        when(paymentHistoryRepository.findByReferenceNumberAndStatus(REF_NUMBER,PaymentStatus.SUBMITTED))
                .thenReturn(Optional.of(history));

        PaymentTaskDetailsDTO dto = new PaymentTaskDetailsDTO();
        dto.setRequestId(REF_NUMBER);
        dto.setBacsAmountPaid(BigDecimal.TEN);

//        when(paymentTaskCompleteResponseFactory.create(any())).thenReturn(new PaymentTaskCompleteResponse(
//
//        ));

        PaymentTaskCompleteResponse response = paymentTaskService.complete(dto, TaskOutcome.APPROVED, "ok");

        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
        assertEquals(BigDecimal.TEN, payment.getAmountPaid());
        verify(paymentRepository).save(payment);
        verify(paymentHistoryRepository).save(history);
        verify(paymentTaskCompleteResponseFactory).create(payment);
    }

    @Test
    void shouldSetPaymentCancelledWhenRejectedBacs() {
        Payment payment = new Payment();
        payment.setReferenceNumber(REF_NUMBER);
        payment.setMethod(PaymentMethod.BACS);

        when(paymentRepository.findByReferenceNumber(REF_NUMBER))
                .thenReturn(Optional.of(payment));
        when(paymentHistoryRepository.findByReferenceNumberAndStatus(REF_NUMBER,PaymentStatus.SUBMITTED))
                .thenReturn(Optional.empty());

        PaymentTaskDetailsDTO dto = new PaymentTaskDetailsDTO();
        dto.setRequestId(REF_NUMBER);

        paymentTaskService.complete(dto, TaskOutcome.REJECTED, "rejected");

        assertEquals(PaymentStatus.CANCELLED, payment.getStatus());
        verify(paymentRepository).save(payment);
        verify(paymentHistoryRepository).save(any(PaymentHistory.class));
    }

    @Test
    void shouldSkipIfNotBacs() {
        Payment payment = new Payment();
        payment.setReferenceNumber(REF_NUMBER);
        payment.setMethod(PaymentMethod.CARD_OR_DIGITAL_WALLET);

        when(paymentRepository.findByReferenceNumber(REF_NUMBER))
                .thenReturn(Optional.of(payment));

        PaymentTaskDetailsDTO dto = new PaymentTaskDetailsDTO();
        dto.setRequestId(REF_NUMBER);

        paymentTaskService.complete(dto, TaskOutcome.APPROVED, "ok");

        verify(paymentRepository, never()).save(any());
        verify(paymentHistoryRepository, never()).save(any());
    }
}
