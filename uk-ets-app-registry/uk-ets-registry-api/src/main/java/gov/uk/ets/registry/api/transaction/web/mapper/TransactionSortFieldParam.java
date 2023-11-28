package gov.uk.ets.registry.api.transaction.web.mapper;

import gov.uk.ets.registry.api.common.search.PageableMapper.SortParameter;
import gov.uk.ets.registry.api.transaction.shared.TransactionPropertyPath;
import gov.uk.ets.registry.api.transaction.shared.TransactionSearchAliases;

import java.util.function.Function;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public enum TransactionSortFieldParam implements SortParameter {
  TRANSACTION_IDENTIFIER("transactionId", direction ->
      Sort.by(direction, TransactionPropertyPath.TRANSACTION_IDENTIFIER)),
  TRANSACTION_QUANTITY("units",
      direction -> Sort.by(direction, TransactionPropertyPath.TRANSACTION_QUANTITY)),
  TRANSACTION_TRANSFERRING_ACCOUNT("transferringAccount",
      direction -> Sort.by(direction, TransactionPropertyPath.TRANSACTION_TRANSFERRING_ACCOUNT)),
  TRANSACTION_ACQUIRING_ACCOUNT("acquiringAccount",
      direction -> Sort.by(direction, TransactionPropertyPath.TRANSACTION_ACQUIRING_ACCOUNT)),
  TRANSACTION_LAST_UPDATED("lastUpdated", direction -> Sort.by(direction, TransactionPropertyPath.TRANSACTION_LAST_UPDATED)),
  TRANSACTION_RUNNING_BALANCE_QUANTITY("runningBalance.quantity", direction -> Sort.by(direction, TransactionSearchAliases.RUNNING_BALANCE_QUANTITY.getAlias())),
  TRANSACTION_STATUS("status", direction -> Sort.by(direction, TransactionPropertyPath.TRANSACTION_STATUS));


  private String sortField;

  private Function<Direction, Sort> getSortFunc;

  TransactionSortFieldParam(String sortField, Function<Sort.Direction, Sort> getSortFunc) {
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