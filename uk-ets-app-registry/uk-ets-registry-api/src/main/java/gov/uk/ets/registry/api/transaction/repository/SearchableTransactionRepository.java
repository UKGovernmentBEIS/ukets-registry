package gov.uk.ets.registry.api.transaction.repository;
import gov.uk.ets.registry.api.transaction.domain.SearchableTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchableTransactionRepository extends JpaRepository<SearchableTransaction, Long>, TransactionProjectionRepository {

}
