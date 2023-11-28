package gov.uk.ets.registry.api.transaction.web.model;

import gov.uk.ets.registry.api.common.search.SearchResult;
import java.io.Serializable;
import java.util.Date;

import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionSearchResult implements SearchResult {
  /**
   * The transaction is
   */
  private String id;
  /**
   * The transaction type
   */
  private String type;
  /**
   * The units info
   */
  private UnitsInfo units;
  /**
   * The Running Balance info.
   */
  private UnitsInfo runningBalance;
  /**
   * The transferring account info
   */
  private AccountInfo transferringAccount;
  /**
   * The acquiring account info
   */
  private AccountInfo acquiringAccount;
  /**
   * The last updated date String representation
   */
  private Date lastUpdated;
  /**
   * The status of the transaction
   */
  private String status;
  
  private String reversedByIdentifier;
  
  private String reversesIdentifier;

  @Getter
  @Builder
  public static class UnitsInfo implements Serializable {
    /**
     * The quantity of units
     */
    private Long quantity;
    /**
     * The units type
     */
    private String type;
  }

  @Getter
  @Builder
  public static class AccountInfo implements Serializable {

    /**
     * The title of the account:
     * - If the account is central then the title is the name of the account
     * - If the account is not central then the title is the full identifier
     */
    private String title;
    /**
     * The business identifier of the UK registry account
     */
    private Long ukRegistryIdentifier;
    /**
     * The business full identifier of the UK registry account
     */
    private String ukRegistryFullIdentifier;
    /**
     * A flag to determine if the account is external
     */
    private boolean isExternalAccount;
    /**
     * Flag to determine if the user has access to the account.
     */
    private boolean userHasAccess;

    private AccountStatus accountStatus;
  }
}
