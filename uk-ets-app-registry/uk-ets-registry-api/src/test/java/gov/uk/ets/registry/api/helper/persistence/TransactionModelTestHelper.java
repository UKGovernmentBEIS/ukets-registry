package gov.uk.ets.registry.api.helper.persistence;

import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.SearchableTransaction;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.EntityManager;
import lombok.Builder;
import lombok.Getter;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@TestComponent
public class TransactionModelTestHelper {
  private EntityManager entityManager;

  public TransactionModelTestHelper(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Builder
  @Getter
  public static class AddTransactionCommand {
    private String identifier;
    private long quantity;
    private LocalDateTime executionDate;
    private TransactionStatus status;
    private TransactionType type;
    private AddAccountInfoCommand addTransferringAccountCommand;
    private AddAccountInfoCommand addAcquiringAccountCommand;
    private UnitType unitType;
    private String lastUpdate;
    private Task task;
    private Date startDate;

    public Date getLastUpdateDate() throws ParseException {
      SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
      return sdf.parse(lastUpdate);
    }
  }

  @Builder
  @Getter
  public static class AddAccountInfoCommand {
    private Long accountIdentifier;
    private KyotoAccountType accountType;
    private String accountRegistryCode;
    private String accountFullIdentifier;
  }

  public SearchableTransaction addSearchableTransaction(AddTransactionCommand command) throws ParseException {
    SearchableTransaction transaction = new SearchableTransaction();
    transaction.setIdentifier(command.identifier);
    transaction.setQuantity(command.quantity);
    transaction.setStatus(command.status);
    transaction.setType(command.type);
    transaction.setUnitType(command.unitType);
    transaction.setTransferringAccount(createAccountInfo(command.addTransferringAccountCommand));
    transaction.setAcquiringAccount(createAccountInfo(command.addAcquiringAccountCommand));
    transaction.setLastUpdated(command.getLastUpdateDate());
    transaction.setStarted(command.startDate);
    transaction.setExecutionDate(command.executionDate);
    entityManager.persist(transaction);
    return transaction;
  }

  public Transaction addTransaction(AddTransactionCommand command) throws ParseException {
    Transaction transaction = new Transaction();
    transaction.setIdentifier(command.identifier);
    transaction.setQuantity(command.quantity);
    transaction.setStatus(command.status);
    transaction.setType(command.type);
    transaction.setUnitType(command.unitType);
    transaction.setTransferringAccount(createAccountInfo(command.addTransferringAccountCommand));
    transaction.setAcquiringAccount(createAccountInfo(command.addAcquiringAccountCommand));
    transaction.setLastUpdated(command.getLastUpdateDate());
    transaction.setStarted(command.startDate);
    transaction.setExecutionDate(command.executionDate);
    entityManager.persist(transaction);
    return transaction;
  }

  public Transaction getTransaction(AddTransactionCommand command) throws ParseException {
    Transaction transaction = new Transaction();
    transaction.setIdentifier(command.identifier);
    transaction.setQuantity(command.quantity);
    transaction.setStatus(command.status);
    transaction.setType(command.type);
    transaction.setUnitType(command.unitType);
    transaction.setTransferringAccount(createAccountInfo(command.addTransferringAccountCommand));
    transaction.setAcquiringAccount(createAccountInfo(command.addAcquiringAccountCommand));
    transaction.setLastUpdated(command.getLastUpdateDate());
    transaction.setStarted(command.startDate);
    transaction.setExecutionDate(command.executionDate);
    return transaction;
  }

  public AccountBasicInfo createAccountInfo(AddAccountInfoCommand command) {
    AccountBasicInfo info = new AccountBasicInfo();
    info.setAccountType(command.accountType);
    info.setAccountRegistryCode(command.accountRegistryCode);
    info.setAccountIdentifier(command.accountIdentifier);
    info.setAccountFullIdentifier(command.accountFullIdentifier);
    return info;
  }

  @Builder
  public static class CreateETSTransactionCommand {
    String identifier;
    Date started;
    LocalDateTime executionDate;
    TransactionStatus status;
  }

  @Transactional
  public Transaction addETSTransaction(CreateETSTransactionCommand command) throws Exception {
    AddTransactionCommand addTransactionCommand = AddTransactionCommand
        .builder()
        .addAcquiringAccountCommand(AddAccountInfoCommand.builder()
            .accountFullIdentifier("acquiring-test-account-key")
            .accountRegistryCode("UK")
            .accountIdentifier(1000L)
            .accountType(KyotoAccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT)
            .build())
        .addTransferringAccountCommand(AddAccountInfoCommand.builder()
            .accountFullIdentifier("transferring-test-account-key")
            .accountIdentifier(2000L)
            .accountRegistryCode("UK")
            .accountType(KyotoAccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT)
            .build())
        .identifier(command.identifier)
        .quantity(20L)
        .status(command.status)
        .startDate(command.started)
        .executionDate(command.executionDate)
        .type(TransactionType.CentralTransferAllowances)
        .unitType(UnitType.AAU)
        .lastUpdate("12/03/2020 13:23")
        .build();
    Transaction transaction = addTransaction(addTransactionCommand);
    return transaction;
  }

}
