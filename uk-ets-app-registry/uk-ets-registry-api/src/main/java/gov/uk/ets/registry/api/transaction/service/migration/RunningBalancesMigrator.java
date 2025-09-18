package gov.uk.ets.registry.api.transaction.service.migration;

import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.transaction.domain.AccountBalance;
import gov.uk.ets.registry.api.transaction.domain.AccountBalanceHistory;
import gov.uk.ets.registry.api.transaction.domain.QTransaction;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.UpdateAccountBalanceResult;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.AccountBalanceHistoryRepository;
import gov.uk.ets.registry.api.transaction.repository.AccountHoldingRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionBlockRepository;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.api.transaction.service.TransactionAccountBalanceService;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
@Log4j2
public class RunningBalancesMigrator implements Migrator {

    private final EntityManager entityManager;
    private final TransactionRepository transactionRepository;
    private final MigratorHistoryRepository migratorHistoryRepository;
    
    /**
     * Repository for account holdings.
     */
    private final AccountHoldingRepository accountHoldingRepository;   
    private final AccountBalanceHistoryRepository accountBalanceHistoryRepository;
    private final TransactionBlockRepository transactionBlockRepository;
    private static final Set<TransactionStatus> COMPLETED = EnumSet.of(TransactionStatus.COMPLETED,
        TransactionStatus.REVERSED);
    private static final Set<String> VALID_COUNTRY_CODES = Stream.of("GB","UK").collect(Collectors.toSet());
    private static final int BATCH_SIZE = 500;
    
    private final TransactionAccountBalanceService transactionAccountBalanceService;
    
    @Override
    @Transactional
    public void migrate() {
        
        log.info("Starting migration of running balances");
        List<MigratorHistory> migratorHistories = migratorHistoryRepository.findByMigratorName(
            MigratorName.RUNNING_BALANCE_MIGRATOR);
        if (!migratorHistories.isEmpty()) {
            log.info("Migration of running balances already performed previously, skipping.");
            return;
        }
        
        //Fetch transactions sorted by lastUpdated DESC
        Slice<Transaction> transactionsSlice = transactionRepository
            .findAllByStatusInOrderByLastUpdatedDesc(TransactionStatus.getFinalStatuses(), PageRequest.of(0, BATCH_SIZE));

        transactionsSlice.getContent().forEach(this::processTransaction);
        while (transactionsSlice.hasNext()) {
            transactionsSlice = transactionRepository
                .findAllByStatusInOrderByLastUpdatedDesc(TransactionStatus.getFinalStatuses(), transactionsSlice.nextPageable());
            transactionsSlice.getContent().forEach(this::processTransaction);
            log.info("Batch was processed. Flushing entity manager...");
            entityManager.flush();
            entityManager.clear();
        }
        
        
        //Fetch all transactions sorted by lastUpdated ASC
        Iterable<Transaction> nonFinalTransactions = transactionRepository
            .findAll(QTransaction.transaction.status.notIn(TransactionStatus.getFinalStatuses()) ,
                Sort.by(Sort.Direction.ASC, "lastUpdated"));

        for (Transaction trns:nonFinalTransactions) {
            log.info("Processing non-finalized transaction identifier: {}",trns.getIdentifier());
            UpdateAccountBalanceResult updateAccountBalanceResult = calculateNonFinalizedTransactionAccountBalances(trns);
            //Also  update the balance per transaction and account
            transactionAccountBalanceService.createTransactionAccountBalances(updateAccountBalanceResult);
        }
        
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.RUNNING_BALANCE_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
        log.info("Migration of of running balances completed");
    }

    private void processTransaction(Transaction trns) {
        log.info("Processing finalized transaction identifier: {}", trns.getIdentifier());
        UpdateAccountBalanceResult updateAccountBalanceResult = calculateFinalizedTransactionAccountBalances(trns);
        //Also  update the balance per transaction and account
        transactionAccountBalanceService.createTransactionAccountBalances(updateAccountBalanceResult);
    }
    
    private UpdateAccountBalanceResult calculateNonFinalizedTransactionAccountBalances(Transaction transaction) {
        //TODO Find the balance per account the instant of transaction lastUpadetDate
        AccountBalance transferringAccountBalance = calculateNonFinalizedTransactionTransferringAccountBalances(transaction)
            .orElse(new AccountBalance(transaction.getTransferringAccount().getIdentifier(),transaction.getQuantity(),transaction.getUnitType()));
        
        AccountBalance acquiringAccountBalance = calculateNonFinalizedTransactionAcquiringAccountOptionalBalances(transaction)
            .orElse(new AccountBalance(transaction.getAcquiringAccount().getIdentifier(),transaction.getQuantity(),transaction.getUnitType()));
        
        return UpdateAccountBalanceResult
            .builder()
            .transactionIdentifier(transaction.getIdentifier())
            .lastUpdated(transaction.getLastUpdated())
            .transferringAccountBalance(transferringAccountBalance)
            .acquiringAccountBalance(acquiringAccountBalance)
            .build();
    }


    private Optional<AccountBalance> calculateNonFinalizedTransactionAcquiringAccountOptionalBalances(Transaction transaction) {
        Optional<Long> acquiringAccountOptional = 
            getAccountIdentifier(transaction.getAcquiringAccount().getAccountFullIdentifier());
        if (acquiringAccountOptional.isPresent()) {
            Long transferringIdentifier = acquiringAccountOptional.get();
            return transactionAccountBalanceService
                .findAccountBalance(transferringIdentifier, transaction.getLastUpdated());            
        }
        return Optional.empty();
    }
    
    private Optional<AccountBalance> calculateNonFinalizedTransactionTransferringAccountBalances(Transaction transaction) {
        Optional<Long> transferringIdentifierOptional = 
            getAccountIdentifier(transaction.getTransferringAccount().getAccountFullIdentifier());
        if (transferringIdentifierOptional.isPresent()) {
            Long transferringIdentifier = transferringIdentifierOptional.get();
            return transactionAccountBalanceService
                .findAccountBalance(transferringIdentifier, transaction.getLastUpdated());            
        }
        return Optional.empty();
    }


    private UpdateAccountBalanceResult calculateFinalizedTransactionAccountBalances(Transaction transaction) {
        
        AccountBalance transferringAccountBalance = calculateTransferringAccountBalance(transaction);
        
        AccountBalance acquiringAccountBalance = calculateAcquiringAccountBalance(transaction);
        
        return UpdateAccountBalanceResult
            .builder()
            .transactionIdentifier(transaction.getIdentifier())
            .lastUpdated(transaction.getLastUpdated())
            .transferringAccountBalance(transferringAccountBalance)
            .acquiringAccountBalance(acquiringAccountBalance)
            .build();
    }


    private AccountBalance calculateAcquiringAccountBalance(Transaction transaction) {
        Optional<Long> acquiringIdentifierOptional = 
            getAccountIdentifier(transaction.getAcquiringAccount().getAccountFullIdentifier());
        AccountBalance acquiringAccountBalance = null;
        
        //Acquiring
        if (acquiringIdentifierOptional.isPresent() && VALID_COUNTRY_CODES.contains(transaction.getAcquiringAccount().getAccountRegistryCode())) {
            Long acquiringIdentifier = acquiringIdentifierOptional.get();
            AccountBalanceHistory acquiringAccBalHistory = findHistoricalBalanceForAccount(acquiringIdentifier);
            //Create the current Balance
            acquiringAccountBalance = new AccountBalance(acquiringIdentifier, 
                acquiringAccBalHistory.getBalance(), 
                acquiringAccBalHistory.getUnitType());
            
            //Move on to calculate the new Balance for the transaction in the loop
         
            //Only migrate amount and UnitType for Completed Transactions
            if (COMPLETED.contains(transaction.getStatus())) {
                
                //Quantity migration
                Long previousBalance = Stream.of(Optional.ofNullable(acquiringAccBalHistory.getBalance()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .orElse(0L);
                if (!isConversion(transaction.getType())) {
                    //when acquiring subtract
                    Long newBalance = previousBalance - transaction.getQuantity();
                    acquiringAccBalHistory.setBalance(newBalance);                        
                }

            
                //Unit type migration
                UnitType previousUnitType = acquiringAccBalHistory.getUnitType();
                UnitType newUnitType = previousUnitType;
                if (!transaction.getUnitType().equals(previousUnitType)) {
                    //We have to check the remaining units that are held , by querying the transactions (blocks)
                    Set<UnitType> remainingUnitTypes = transactionBlockRepository.
                        findUnitTypesByAcquiringAccountAndStatusInAndLastUpdatedBefore(
                        acquiringIdentifier, 
                        COMPLETED, 
                        transaction.getLastUpdated());
                    if (remainingUnitTypes.size() > 1) {
                        newUnitType = UnitType.MULTIPLE;
                    } else if (remainingUnitTypes.size() == 1) {
                        newUnitType = remainingUnitTypes.iterator().next();
                    } else {
                        //No units left
                        newUnitType = null;
                    }
                } else if (acquiringAccBalHistory.getBalance() == 0) {
                    newUnitType = null;
                }                
                acquiringAccBalHistory.setUnitType(newUnitType);
                //end Unit type migration
                
            } //End if  COMPLETED,REVERSED
            acquiringAccBalHistory.setTransactionIdentifier(transaction.getIdentifier());        
            
            accountBalanceHistoryRepository.save(acquiringAccBalHistory);
        }
        return acquiringAccountBalance;
    }


    private AccountBalance calculateTransferringAccountBalance(Transaction transaction) {
        Optional<Long> transferringIdentifierOptional = 
            getAccountIdentifier(transaction.getTransferringAccount().getAccountFullIdentifier());
        AccountBalance transferringAccountBalance = null;
        
        //Transferring
        if (transferringIdentifierOptional.isPresent() && VALID_COUNTRY_CODES.contains(transaction.getTransferringAccount().getAccountRegistryCode())) {
            Long transferringIdentifier = transferringIdentifierOptional.get();
            AccountBalanceHistory transferringAccBalHistory = findHistoricalBalanceForAccount(transferringIdentifier);
            
            //Record the current situation in Transaction Account Balance and then calculate the new balance
            transferringAccountBalance = new AccountBalance(transferringIdentifier, 
                transferringAccBalHistory.getBalance(), 
                transferringAccBalHistory.getUnitType());
            
            //Move on to calculate the new Balance for the transaction in the loop
           
            if (COMPLETED.contains(transaction.getStatus()) &&
                !isIssuance(transaction.getType())) {
               
                Long previousBalance = Stream.of(Optional.ofNullable(transferringAccBalHistory.getBalance()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .orElse(0L);
                if (!isConversion(transaction.getType())) {
                    //when transferring add
                    Long newBalance = previousBalance + transaction.getQuantity(); 
                    transferringAccBalHistory.setBalance(newBalance);                        
                }
                
                //Unit type migration
                UnitType previousUnitType = transferringAccBalHistory.getUnitType();
                UnitType newUnitType = previousUnitType;
                if (!transaction.getUnitType().equals(previousUnitType)) {
                    //A unit type was added , set as MULTIPLE
                    newUnitType = UnitType.MULTIPLE;
                } else if (transferringAccBalHistory.getBalance() == 0) {
                    //No units left
                    newUnitType = null;
                }
                
                transferringAccBalHistory.setUnitType(newUnitType);
                //end Unit type migration
                
            } //End if  COMPLETED,REVERSED
            transferringAccBalHistory.setTransactionIdentifier(transaction.getIdentifier());
            
            accountBalanceHistoryRepository.save(transferringAccBalHistory);
        } //end if transferringIdentifierOptional.isPresent()
        return transferringAccountBalance;
    }


    //Returns the existing AccountBalanceHistory or creates a new one if it does
    //not exists.The initial data reflect the current situation of the account.
    private AccountBalanceHistory findHistoricalBalanceForAccount(Long accountIdentifier) {

        return accountBalanceHistoryRepository.findByAccountIdentifier(accountIdentifier).orElseGet(() -> {
            AccountBalanceHistory history = new AccountBalanceHistory();
            history.setAccountIdentifier(accountIdentifier);
            history.setUnitType(getAccountUnitType(accountIdentifier));
            history.setBalance(Optional.ofNullable(accountHoldingRepository.getAccountBalance(accountIdentifier)).orElse(0L));
            return history;
        });
    }    
    
    //Fetch the unit type by querying the UnitBlock table
    private UnitType getAccountUnitType(Long accountIdentifier) {
        UnitType unitType = null;
        List<UnitType> unitTypes = accountHoldingRepository.getAccountUnitTypes(accountIdentifier);
        if (!CollectionUtils.isEmpty(unitTypes)) {
            unitType = UnitType.MULTIPLE;
            if (unitTypes.size() == 1) {
                unitType = unitTypes.get(0);
            }
        }        
        
        return unitType;
    }
    
    private Optional<Long> getAccountIdentifier(String accountFullIdentifier) {
        return Optional.ofNullable(accountHoldingRepository.getAccountIdentifier(accountFullIdentifier));
    }
 
    /**
     * Whether the type is Issuance of any type or not.
     *
     * @return true/false
     */
    private boolean isIssuance(TransactionType transactionType) {
        return EnumSet.of(TransactionType.IssueOfAAUsAndRMUs,
            TransactionType.IssueAllowances,
            TransactionType.IssuanceCP0,
            TransactionType.IssuanceDecoupling
            ).contains(transactionType);
    }
    
    /**
     * Whether the type is Conversion of any type or not.
     *
     * @return true/false
     */
    private boolean isConversion(TransactionType transactionType) {
        return EnumSet.of(TransactionType.ConversionA,
            TransactionType.ConversionB,
            TransactionType.ConversionCP1,
            TransactionType.ConversionOfSurrenderedFormerEUA
            ).contains(transactionType);      
    }
}
