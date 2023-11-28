package gov.uk.ets.registry.api.account.web.model.search;

import gov.uk.ets.registry.api.account.shared.AccountProjection;
import gov.uk.ets.registry.api.account.web.mappers.AccountHolderNameFromProjectionMapper;
import gov.uk.ets.registry.api.common.search.SearchResult;
import lombok.Getter;
import lombok.Setter;

/**
 * The Data transfer object for account search result rows
 */
@Getter
@Setter
public class AccountSearchResult implements SearchResult {
  public static AccountSearchResult of(AccountProjection account) {
    AccountSearchResult result = new AccountSearchResult();
    result.setAccountId(account.getIdentifier());
    result.setFullAccountNo(account.getFullIdentifier());
    result.setAccountName(account.getAccountName());
    result.setAccountType(account.getTypeLabel());
    result.setAccountHolderName(new AccountHolderNameFromProjectionMapper().map(account));
    result.setAccountStatus(account.getAccountStatus() != null ?
            account.getAccountStatus().name() : null);
    result.setComplianceStatus(account.getComplianceStatus() != null ?
            account.getComplianceStatus().name() : null);
    result.setBalance(account.getBalance());
    return result;
  }
  /**
   * The account identifier;
   */
  private Long accountId;
  /**
   * The full account id ([registry code]-[kyoto account type code]-[account identifier]-[account
   * check digits])
   */
  private String fullAccountNo;
  /**
   * The account name
   */
  private String accountName;
  /**
   * The label of the account type which is determined by the combination of one kyoto account type
   * and one registry account type.
   */
  private String accountType;
  /**
   * The account holder name
   */
  private String accountHolderName;
  /**
   * The account status
   */
  private String accountStatus;
  /**
   * The compliance status
   */
  private String complianceStatus;
  /**
   * The account balance
   */
  private Long balance;
}
