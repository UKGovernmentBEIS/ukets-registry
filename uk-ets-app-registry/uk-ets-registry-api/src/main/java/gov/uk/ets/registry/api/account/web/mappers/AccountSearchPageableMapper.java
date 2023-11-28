package gov.uk.ets.registry.api.account.web.mappers;

import gov.uk.ets.registry.api.account.shared.AccountPropertyPath;
import gov.uk.ets.registry.api.common.search.PageableMapper;
import gov.uk.ets.registry.api.common.search.PageParameters;
import java.util.function.Function;

import org.springframework.data.domain.Sort;

/**
 * Mapper which responsibility is to map the received {@link PageParameters} params to a {@link
 * org.springframework.data.domain.Pageable} object
 */
public class AccountSearchPageableMapper extends PageableMapper {

  public AccountSearchPageableMapper() {
    super(SortFieldParam.values(), SortFieldParam.ACCOUNT_HOLDER_NAME);
  }

  /**
   * The Sort field param enum. It maps a sort filed request parameter to a {@link Sort} object.
   */
  public enum SortFieldParam implements SortParameter {
    ACCOUNT_FULL_IDENTIFIER("accountId", direction ->
        Sort.by(direction, AccountPropertyPath.ACCOUNT_FULL_IDENTIFIER)),
    ACCOUNT_HOLDER_NAME("accountHolderName",
        direction -> Sort.by(direction,
            AccountPropertyPath.ACCOUNT_HOLDER_NAME)),
    ACCOUNT_TYPE_LABEL("accountType",
        direction -> Sort.by(direction, AccountPropertyPath.ACCOUNT_TYPE_LABEL)),
    ACCOUNT_NAME("accountName",
        direction -> Sort.by(direction,
            AccountPropertyPath.ACCOUNT_NAME)),
    ACCOUNT_STATUS("accountStatus",
        direction -> Sort.by(direction,
            AccountPropertyPath.ACCOUNT_STATUS)),
    ACCOUNT_COMPLIANCE_STATUS("complianceStatus",
        direction -> Sort.by(direction,
            AccountPropertyPath.ACCOUNT_COMPLIANCE_STATUS)),
    ACCOUNT_BALANCE("balance", direction -> Sort.by(direction,
        AccountPropertyPath.ACCOUNT_BALANCE));

    private String sortField;

    private Function<Sort.Direction, Sort> getSortFunc;

    SortFieldParam(String sortField, Function<Sort.Direction, Sort> getSortFunc) {
      this.sortField = sortField;
      this.getSortFunc = getSortFunc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
      return sortField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Function<Sort.Direction, Sort> getSortProvider() {
      return getSortFunc;
    }
  }
}