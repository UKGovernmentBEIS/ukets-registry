package gov.uk.ets.registry.api.payment.service.mapper;

import gov.uk.ets.registry.api.payment.domain.Payment;
import gov.uk.ets.registry.api.payment.domain.PaymentHistory;
import gov.uk.ets.registry.api.payment.domain.types.PaymentHistoryType;
import gov.uk.ets.registry.api.payment.domain.types.PaymentMethod;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import gov.uk.ets.registry.api.payment.web.model.PaymentHistoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentHistoryMapperTest {

    private PaymentHistoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PaymentHistoryMapper();
    }

    @Test
    void map_shouldMapPaymentToPaymentHistory() {
        Payment payment = new Payment();
        payment.setAmountPaid(BigDecimal.valueOf(120.50));
        payment.setPaymentId("PAY123");
        payment.setReferenceNumber(300189L);
        payment.setMethod(PaymentMethod.CARD_OR_DIGITAL_WALLET);
        payment.setStatus(PaymentStatus.CANCELLED);
        LocalDateTime now = LocalDateTime.now();
        payment.setUpdated(now);

        PaymentHistory history = mapper.map(payment);

        assertThat(history.getAmount()).isEqualByComparingTo("120.50");
        assertThat(history.getPaymentId()).isEqualTo("PAY123");
        assertThat(history.getReferenceNumber()).isEqualTo(300189L);
        assertThat(history.getMethod()).isEqualTo(PaymentMethod.CARD_OR_DIGITAL_WALLET);
        assertThat(history.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(history.getUpdated()).isEqualTo(now);
    }

    @Test
    void toDto_shouldMapListOfPaymentHistories() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();

        PaymentHistory history = new PaymentHistory();
        history.setId(1L);
        history.setType(PaymentHistoryType.PAYMENT);
        history.setStatus(PaymentStatus.SUCCESS);
        history.setPaymentId("PAY456");
        history.setAmount(BigDecimal.valueOf(200));
        history.setReferenceNumber(123456L);
        history.setCreated(created);
        history.setUpdated(updated);
        history.setMethod(PaymentMethod.BACS);

        List<PaymentHistory> histories = List.of(history);

        List<PaymentHistoryDTO> result = mapper.toDto(histories);

        assertThat(result).hasSize(1);
        PaymentHistoryDTO dto = result.get(0);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getType()).isEqualTo(PaymentHistoryType.PAYMENT);
        assertThat(dto.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(dto.getPaymentId()).isEqualTo("PAY456");
        assertThat(dto.getAmount()).isEqualByComparingTo("200");
        assertThat(dto.getReferenceNumber()).isEqualTo(123456L);
        assertThat(dto.getCreated()).isEqualTo(created);
        assertThat(dto.getUpdated()).isEqualTo(updated);
        assertThat(dto.getMethod()).isEqualTo(PaymentMethod.BACS);
    }
}
