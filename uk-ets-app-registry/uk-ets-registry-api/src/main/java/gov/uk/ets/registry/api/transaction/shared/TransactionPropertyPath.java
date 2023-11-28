package gov.uk.ets.registry.api.transaction.shared;

/**
 * Transaction property paths
 */
public class TransactionPropertyPath {
  private TransactionPropertyPath() {}

  /**
   * The full identifier property path
   */
  public static final String TRANSACTION_IDENTIFIER = "transaction.identifier";
  /**
   * The quantity of units in transaction
   */
  public static final String TRANSACTION_QUANTITY = "transaction.quantity";
  /**
   * The full identifier of the transferring account
   */
  public static final String TRANSACTION_TRANSFERRING_ACCOUNT = "transferringAccount.fullIdentifier";
  /**
   * The full identifier of the acquiring account
   */
  public static final String TRANSACTION_ACQUIRING_ACCOUNT = "acquiringAccount.fullIdentifier";
  /**
   * The last updated date
   */
  public static final String TRANSACTION_LAST_UPDATED = "transaction.lastUpdated";
  /**
   * The status of the transaction
   */
  public static final String TRANSACTION_STATUS = "transaction.status";
}
