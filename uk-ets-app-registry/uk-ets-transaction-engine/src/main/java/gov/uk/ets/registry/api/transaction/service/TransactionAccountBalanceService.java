package gov.uk.ets.registry.api.transaction.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;

import gov.uk.ets.registry.api.transaction.domain.AccountBalance;
import gov.uk.ets.registry.api.transaction.domain.TransactionAccountBalance;
import gov.uk.ets.registry.api.transaction.domain.UpdateAccountBalanceResult;
import gov.uk.ets.registry.api.transaction.repository.TransactionAccountBalanceRepository;
import lombok.AllArgsConstructor;

/**
 * Service for maintaining the account balances per transaction.
 */
@Service
@AllArgsConstructor
public class TransactionAccountBalanceService {
    
    /**
     * Repository for account holdings.
     */
    private final TransactionAccountBalanceRepository transactionAccountBalanceRepository;    
    
    
    public Optional<AccountBalance> findAccountBalance(Long accountIdentifier,Date balanaceDate) {
        return transactionAccountBalanceRepository.findByAccountIdentifierAndDate(accountIdentifier, balanaceDate);   
    }
    
    /**
     * Creates the account balances after transaction proposal.
     * 
     * @param result
     */
    public void createTransactionAccountBalances(UpdateAccountBalanceResult result) {

        TransactionAccountBalance transactionAccountBalance =
            transactionAccountBalanceRepository.findByTransactionIdentifier(result.getTransactionIdentifier())
                .map(tab -> {
                    tab.setLastUpdated(result.getLastUpdated());
                    return tab;
                })
                .orElseGet(() -> {
                    TransactionAccountBalance tab = new TransactionAccountBalance();
                    tab.setTransactionIdentifier(result.getTransactionIdentifier());
                    tab.setLastUpdated(result.getLastUpdated());
                    return tab;
                });
        
        updateAndSaveTransactionAccountBalance(result,transactionAccountBalance);
        
    }
    
    /**
     * Maintain the account balances after transaction finalization.
     * 
     * @param result
     */
    public void updateTransactionAccountBalances(UpdateAccountBalanceResult result) {
        
        TransactionAccountBalance transactionAccountBalance = transactionAccountBalanceRepository.findByTransactionIdentifier(result.getTransactionIdentifier())
            .orElseThrow(IllegalArgumentException::new);
        
        updateAndSaveTransactionAccountBalance(result,transactionAccountBalance);
        
    }

    private void updateAndSaveTransactionAccountBalance(UpdateAccountBalanceResult result,TransactionAccountBalance accountsBalances) {
        setTransferringAccountTransactionBalance(Optional.ofNullable(result.getTransferringAccountBalance()), accountsBalances);
        
        setAcquiringAccountTransactionBalance(Optional.ofNullable(result.getAcquiringAccountBalance()), accountsBalances);
        
        transactionAccountBalanceRepository.save(accountsBalances);        
    }
    
    private void setTransferringAccountTransactionBalance(Optional<AccountBalance> transferringAccountOptional,TransactionAccountBalance transactionAccountBalance) {
        
        if (transferringAccountOptional.isPresent()) {
            AccountBalance transferringAccount = transferringAccountOptional.get();
            transactionAccountBalance.setTransferringAccountIdentifier(transferringAccount.getIdentifier());           
            transactionAccountBalance.setTransferringAccountBalance(transferringAccount.getBalance());
            transactionAccountBalance.setTransferringAccountBalanceUnitType(transferringAccount.getUnitType());
        }        
    }
    
    private void setAcquiringAccountTransactionBalance(Optional<AccountBalance> acquiringAccountOptional,TransactionAccountBalance transactionAccountBalance) {
        
        if (acquiringAccountOptional.isPresent()) {
            AccountBalance acquiringAccount = acquiringAccountOptional.get();
            transactionAccountBalance.setAcquiringAccountIdentifier(acquiringAccount.getIdentifier());         
            transactionAccountBalance.setAcquiringAccountBalance(acquiringAccount.getBalance());
            transactionAccountBalance.setAcquiringAccountBalanceUnitType(acquiringAccount.getUnitType());
        }     
    }
}
