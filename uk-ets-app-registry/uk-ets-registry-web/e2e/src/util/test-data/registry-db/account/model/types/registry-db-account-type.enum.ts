import { RegistryAccountType } from './registry-db-registry-account-type.enum';
import { KyotoAccountType } from './registry-db-kyoto-account-type.enum';

export enum AccountType {
  /**
   * The Operator holding account (ETS) type
   */
  OPERATOR_HOLDING_ACCOUNT,

  /**
   * The Aircraft operator holding account (ETS) type
   */
  AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,

  /**
   * The Trading account (ETS) type
   */
  TRADING_ACCOUNT,

  /**
   * The UK Auction delivery account type
   */
  UK_AUCTION_DELIVERY_ACCOUNT,

  /**
   * The Person holding account (KP) type
   */
  PERSON_HOLDING_ACCOUNT,

  /**
   * Party holding account (KP)
   */
  PARTY_HOLDING_ACCOUNT,

  /**
   * The Former operator holding account type
   */
  FORMER_OPERATOR_HOLDING_ACCOUNT,

  /**
   * The UK surrender account type
   */
  UK_SURRENDER_ACCOUNT,
}

export function getKyotoType(accountType: AccountType): KyotoAccountType {
  switch (accountType) {
    case AccountType.OPERATOR_HOLDING_ACCOUNT:
      return KyotoAccountType.PARTY_HOLDING_ACCOUNT;
    case AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT:
      return KyotoAccountType.PARTY_HOLDING_ACCOUNT;
    case AccountType.TRADING_ACCOUNT:
      return KyotoAccountType.PARTY_HOLDING_ACCOUNT;
    case AccountType.UK_AUCTION_DELIVERY_ACCOUNT:
      return KyotoAccountType.PARTY_HOLDING_ACCOUNT;
    case AccountType.PERSON_HOLDING_ACCOUNT:
      return KyotoAccountType.PERSON_HOLDING_ACCOUNT;
    case AccountType.PARTY_HOLDING_ACCOUNT:
      return KyotoAccountType.PARTY_HOLDING_ACCOUNT;
    case AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT:
      return KyotoAccountType.FORMER_OPERATOR_HOLDING_ACCOUNT;
  }
}

export function getRegistryType(accountType: AccountType): RegistryAccountType {
  switch (accountType) {
    case AccountType.OPERATOR_HOLDING_ACCOUNT:
      return RegistryAccountType.OPERATOR_HOLDING_ACCOUNT;
    case AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT:
      return RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT;
    case AccountType.TRADING_ACCOUNT:
      return RegistryAccountType.TRADING_ACCOUNT;
    case AccountType.UK_AUCTION_DELIVERY_ACCOUNT:
      return RegistryAccountType.UK_AUCTION_DELIVERY_ACCOUNT;
    case AccountType.PERSON_HOLDING_ACCOUNT:
      return RegistryAccountType.NONE;
    case AccountType.PARTY_HOLDING_ACCOUNT:
      return RegistryAccountType.NONE;
    case AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT:
      return RegistryAccountType.NONE;
  }
}
