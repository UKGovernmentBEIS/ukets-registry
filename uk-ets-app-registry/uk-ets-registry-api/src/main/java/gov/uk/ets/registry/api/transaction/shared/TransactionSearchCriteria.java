package gov.uk.ets.registry.api.transaction.shared;

import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * The transaction search criteria
 */
@Getter
@Setter
public class TransactionSearchCriteria implements Serializable {

  /**
   * The transaction id
   */
  @Size(min = 3)
  private String transactionId;

  /**
   * The transaction type
   */
  private String transactionType;

  /**
   * The transaction status
   */
  private String transactionStatus;

  /**
   * The transaction last update date (from)
   */
  @DateTimeFormat(iso=ISO.DATE)
  private Date transactionLastUpdateDateFrom;

  /**
   * The transaction last update date (until)
   */
  @DateTimeFormat(iso=ISO.DATE)
  private Date transactionLastUpdateDateTo;

  /**
   * The transferring account number
   */
  @Size(min = 3)
  private String transferringAccountNumber;

  /**
   * The acquiring account number
   */
  @Size(min = 3)
  private String acquiringAccountNumber;

  /**
   * The acquiring account type
   */
  private String acquiringAccountType;

  /**
   * The transferring account type
   */
  private String transferringAccountType;

  /**
   * The unit type
   */
  private String unitType;

  /**
   * The initiator user id
   */
  @Size(min = 3)
  private String initiatorUserId;

  /**
   * The approver user id
   */
  @Size(min = 3)
  private String approverUserId;

  /**
   * The transactional proposal date (from)
   */
  @DateTimeFormat(iso = ISO.DATE)
  private Date transactionalProposalDateFrom;

  /**
   * The transactional proposal date (until)
   */
  @DateTimeFormat(iso = ISO.DATE)
  private Date transactionalProposalDateTo;
  
  /**
   * True if we want to display running balances.
   */
  private boolean showRunningBalances;
  
  /**
   * Whether the search should return reversed transactions.
   */
  private Boolean reversed;
}
