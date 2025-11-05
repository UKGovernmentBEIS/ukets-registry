package gov.uk.ets.registry.api.payment.repository;

import gov.uk.ets.registry.api.payment.domain.PaymentHistory;
import gov.uk.ets.registry.api.payment.domain.types.PaymentStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long>,PaymentHistorySearchRepository {

    Optional<PaymentHistory> findByReferenceNumberAndStatus(Long requestId, PaymentStatus status);

    Page<PaymentHistory> findByReferenceNumberTextContaining(String part, Pageable pageable);
}
