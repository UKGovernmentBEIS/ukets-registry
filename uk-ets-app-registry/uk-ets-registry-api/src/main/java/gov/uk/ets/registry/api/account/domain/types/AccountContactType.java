package gov.uk.ets.registry.api.account.domain.types;

public enum AccountContactType {
  /**
   * Indicates that the {@link gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative}
   * is the primary contact of the account.
   */
  PRIMARY,
  /**
   * Indicates that the {@link gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative}
   * is the secondary contact of the account.
   */
  ALTERNATIVE
}
