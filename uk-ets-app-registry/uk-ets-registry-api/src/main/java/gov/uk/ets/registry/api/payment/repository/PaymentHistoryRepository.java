package gov.uk.ets.registry.api.payment.repository;

import gov.uk.ets.registry.api.payment.domain.PaymentHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    Page<PaymentHistory> findByReferenceNumberTextContaining(
            String part,
            Pageable pageable
    );
}
