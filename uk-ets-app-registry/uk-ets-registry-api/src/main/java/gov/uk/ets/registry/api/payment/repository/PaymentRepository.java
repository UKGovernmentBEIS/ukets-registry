package gov.uk.ets.registry.api.payment.repository;

import gov.uk.ets.registry.api.payment.domain.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReferenceNumber(Long referenceNumber);
    
    Optional<Payment> findByUrlSuffix(String urlSuffix);
}
