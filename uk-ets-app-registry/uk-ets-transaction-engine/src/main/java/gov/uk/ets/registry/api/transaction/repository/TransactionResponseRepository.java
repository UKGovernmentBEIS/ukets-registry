package gov.uk.ets.registry.api.transaction.repository;

import gov.uk.ets.registry.api.transaction.domain.TransactionResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionResponseRepository extends JpaRepository<TransactionResponse, Long> {

}
