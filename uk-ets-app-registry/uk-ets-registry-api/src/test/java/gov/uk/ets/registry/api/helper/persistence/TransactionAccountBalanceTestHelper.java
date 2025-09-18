package gov.uk.ets.registry.api.helper.persistence;

import gov.uk.ets.registry.api.transaction.domain.TransactionAccountBalance;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.Date;
import jakarta.persistence.EntityManager;
import lombok.Builder;
import lombok.Getter;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
public class TransactionAccountBalanceTestHelper {

    private EntityManager entityManager;

    public TransactionAccountBalanceTestHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @Transactional
    public TransactionAccountBalance addTransactionAccountBalance(AddTransactionAccountBalanceCommand command) {
        
        TransactionAccountBalance transactionAccountBalance = new TransactionAccountBalance();
        transactionAccountBalance.setTransactionIdentifier(command.transactionIdentifier);
        
        transactionAccountBalance.setTransferringAccountIdentifier(command.getTransferringAccountIdentifer());
        transactionAccountBalance.setTransferringAccountBalance(command.getTransferringAccountBalance());
        transactionAccountBalance.setTransferringAccountBalanceUnitType(command.getTransferringAccountBalanceUnitType());
        transactionAccountBalance.setAcquiringAccountIdentifier(command.getTransferringAccountIdentifer());
        transactionAccountBalance.setAcquiringAccountBalance(command.getTransferringAccountBalance());
        transactionAccountBalance.setAcquiringAccountBalanceUnitType(command.getTransferringAccountBalanceUnitType());
        transactionAccountBalance.setLastUpdated(command.getLastUpdated());
        
        entityManager.persist(transactionAccountBalance);
        
        return transactionAccountBalance;
    }
    
    @Builder
    @Getter
    public static class AddTransactionAccountBalanceCommand {
        private String transactionIdentifier;
        private Long transferringAccountIdentifer;
        private Long transferringAccountBalance;
        private UnitType transferringAccountBalanceUnitType;
        private Long acquiringAccountIdentifer;
        private Long acquiringAccountBalance;
        private UnitType acquiringAccountBalanceUnitType;
        
        private Date lastUpdated;

    }
    
}
