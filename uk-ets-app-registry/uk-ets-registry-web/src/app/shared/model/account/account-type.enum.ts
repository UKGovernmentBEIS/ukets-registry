export enum AccountType {
  OPERATOR_HOLDING_ACCOUNT = 'OPERATOR_HOLDING_ACCOUNT',
  AIRCRAFT_OPERATOR_HOLDING_ACCOUNT = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT',
  MARITIME_OPERATOR_HOLDING_ACCOUNT = 'MARITIME_OPERATOR_HOLDING_ACCOUNT',
  TRADING_ACCOUNT = 'TRADING_ACCOUNT',
  UK_AUCTION_DELIVERY_ACCOUNT = 'UK_AUCTION_DELIVERY_ACCOUNT',
  PERSON_HOLDING_ACCOUNT = 'PERSON_HOLDING_ACCOUNT',
  PARTY_HOLDING_ACCOUNT = 'PARTY_HOLDING_ACCOUNT',
  FORMER_OPERATOR_HOLDING_ACCOUNT = 'FORMER_OPERATOR_HOLDING_ACCOUNT',
  PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT = 'PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT',
  NET_SOURCE_CANCELLATION_ACCOUNT = 'NET_SOURCE_CANCELLATION_ACCOUNT',
  NON_COMPLIANCE_CANCELLATION_ACCOUNT = 'NON_COMPLIANCE_CANCELLATION_ACCOUNT',
  UK_SURRENDER_ACCOUNT = 'UK_SURRENDER_ACCOUNT',
  UK_TOTAL_QUANTITY_ACCOUNT = 'UK_TOTAL_QUANTITY_ACCOUNT',
  UK_AVIATION_TOTAL_QUANTITY_ACCOUNT = 'UK_AVIATION_TOTAL_QUANTITY_ACCOUNT',
  UK_AUCTION_ACCOUNT = 'UK_AUCTION_ACCOUNT',
  UK_DELETION_ACCOUNT = 'UK_DELETION_ACCOUNT',
  UK_ALLOCATION_ACCOUNT = 'UK_ALLOCATION_ACCOUNT',
  UK_NEW_ENTRANTS_RESERVE_ACCOUNT = 'UK_NEW_ENTRANTS_RESERVE_ACCOUNT',
  UK_MARKET_STABILITY_MECHANISM_ACCOUNT = 'UK_MARKET_STABILITY_MECHANISM_ACCOUNT',
  UK_GENERAL_HOLDING_ACCOUNT = 'UK_GENERAL_HOLDING_ACCOUNT',
  NATIONAL_HOLDING_ACCOUNT = 'NATIONAL_HOLDING_ACCOUNT',
  RETIREMENT_ACCOUNT = 'RETIREMENT_ACCOUNT',
  VOLUNTARY_CANCELLATION_ACCOUNT = 'VOLUNTARY_CANCELLATION_ACCOUNT',
  MANDATORY_CANCELLATION_ACCOUNT = 'MANDATORY_CANCELLATION_ACCOUNT',
  ARTICLE_3_7_TER_CANCELLATION_ACCOUNT = 'ARTICLE_3_7_TER_CANCELLATION_ACCOUNT',
  AMBITION_INCREASE_CANCELLATION_ACCOUNT = 'AMBITION_INCREASE_CANCELLATION_ACCOUNT',
  TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY = 'TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY',
  LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY = 'LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY',
  LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE = 'LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE',
  // eslint-disable-next-line max-len
  LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT = 'LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT',
  EXCESS_ISSUANCE_CANCELLATION_ACCOUNT = 'EXCESS_ISSUANCE_CANCELLATION_ACCOUNT',
  CCS_NET_REVERSAL_CANCELLATION_ACCOUNT = 'CCS_NET_REVERSAL_CANCELLATION_ACCOUNT',
  CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT = 'CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT',
  PENDING_ACCOUNT = 'PENDING_ACCOUNT',
  AAU_DEPOSIT = 'AAU_DEPOSIT',
}

export enum RegistryAccountType {
  UK_TOTAL_QUANTITY_ACCOUNT = 'UK_TOTAL_QUANTITY_ACCOUNT',
  UK_AVIATION_TOTAL_QUANTITY_ACCOUNT = 'UK_AVIATION_TOTAL_QUANTITY_ACCOUNT',
  UK_AUCTION_ACCOUNT = 'UK_AUCTION_ACCOUNT',
  UK_ALLOCATION_ACCOUNT = 'UK_ALLOCATION_ACCOUNT',
  UK_NEW_ENTRANTS_RESERVE_ACCOUNT = 'UK_NEW_ENTRANTS_RESERVE_ACCOUNT',
  UK_AVIATION_ALLOCATION_ACCOUNT = 'UK_AVIATION_ALLOCATION_ACCOUNT',
  UK_DELETION_ACCOUNT = 'UK_DELETION_ACCOUNT',
  UK_SURRENDER_ACCOUNT = 'UK_SURRENDER_ACCOUNT',
  UK_MARKET_STABILITY_MECHANISM_ACCOUNT = 'UK_MARKET_STABILITY_MECHANISM_ACCOUNT',
  UK_GENERAL_HOLDING_ACCOUNT = 'UK_GENERAL_HOLDING_ACCOUNT',
  UK_AUCTION_DELIVERY_ACCOUNT = 'UK_AUCTION_DELIVERY_ACCOUNT',
  OPERATOR_HOLDING_ACCOUNT = 'OPERATOR_HOLDING_ACCOUNT',
  AIRCRAFT_OPERATOR_HOLDING_ACCOUNT = 'AIRCRAFT_OPERATOR_HOLDING_ACCOUNT',
  MARITIME_OPERATOR_HOLDING_ACCOUNT = 'MARITIME_OPERATOR_HOLDING_ACCOUNT',
  NATIONAL_HOLDING_ACCOUNT = 'NATIONAL_HOLDING_ACCOUNT',
  TRADING_ACCOUNT = 'TRADING_ACCOUNT',
  AAU_DEPOSIT = 'AAU_DEPOSIT',
  NONE = 'NONE',
}

export enum KyotoAccountType {
  PARTY_HOLDING_ACCOUNT = 'PARTY_HOLDING_ACCOUNT',
  FORMER_OPERATOR_HOLDING_ACCOUNT = 'FORMER_OPERATOR_HOLDING_ACCOUNT',
  PERSON_HOLDING_ACCOUNT = 'PERSON_HOLDING_ACCOUNT',
  PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT = 'PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT',
  NET_SOURCE_CANCELLATION_ACCOUNT = 'NET_SOURCE_CANCELLATION_ACCOUNT',
  NON_COMPLIANCE_CANCELLATION_ACCOUNT = 'NON_COMPLIANCE_CANCELLATION_ACCOUNT',
  VOLUNTARY_CANCELLATION_ACCOUNT = 'VOLUNTARY_CANCELLATION_ACCOUNT',
  EXCESS_ISSUANCE_CANCELLATION_ACCOUNT = 'EXCESS_ISSUANCE_CANCELLATION_ACCOUNT',
  CCS_NET_REVERSAL_CANCELLATION_ACCOUNT = 'CCS_NET_REVERSAL_CANCELLATION_ACCOUNT',
  CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT = 'CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT',
  MANDATORY_CANCELLATION_ACCOUNT = 'MANDATORY_CANCELLATION_ACCOUNT',
  ADMINISTRATIVE_CANCELLATION_ACCOUNT = 'ADMINISTRATIVE_CANCELLATION_ACCOUNT',
  ARTICLE_3_7_TER_CANCELLATION_ACCOUNT = 'ARTICLE_3_7_TER_CANCELLATION_ACCOUNT',
  AMBITION_INCREASE_CANCELLATION_ACCOUNT = 'AMBITION_INCREASE_CANCELLATION_ACCOUNT',
  RETIREMENT_ACCOUNT = 'RETIREMENT_ACCOUNT',
  TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY = 'TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY',
  LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY = 'LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY',
  LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE = 'LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE',
  // eslint-disable-next-line max-len
  LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT = 'LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT',
  PENDING_ACCOUNT = 'PENDING_ACCOUNT',
}

export interface AccountTypeValues {
  label: string;
  registryAccountType: RegistryAccountType;
  kyotoAccountType: KyotoAccountType;
  isKyoto?: boolean;
}

export const AccountTypeMap: Record<AccountType, AccountTypeValues> = {
  [AccountType.OPERATOR_HOLDING_ACCOUNT]: {
    label: 'ETS - Operator Holding Account',
    registryAccountType: RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT]: {
    label: 'ETS - Aircraft Operator Holding Account',
    registryAccountType: RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT]: {
    label: 'ETS - Maritime Operator Holding Account',
    registryAccountType: RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.TRADING_ACCOUNT]: {
    label: 'ETS - Trading Account',
    registryAccountType: RegistryAccountType.TRADING_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.UK_AUCTION_DELIVERY_ACCOUNT]: {
    label: 'ETS - UK Auction delivery account',
    registryAccountType: RegistryAccountType.UK_AUCTION_DELIVERY_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.PERSON_HOLDING_ACCOUNT]: {
    label: 'KP - Person Holding Account',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
    isKyoto: true,
  },

  [AccountType.PARTY_HOLDING_ACCOUNT]: {
    label: 'KP - Party holding account',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
    isKyoto: true,
  },
  [AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT]: {
    label: 'KP - Former operator holding account',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.FORMER_OPERATOR_HOLDING_ACCOUNT,
    isKyoto: true,
  },
  [AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT]: {
    label: 'KP - Previous Period Surplus Reserve account (PPSR)',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT,
    isKyoto: true,
  },

  [AccountType.NET_SOURCE_CANCELLATION_ACCOUNT]: {
    label: 'KP - Net source cancellation account',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.NET_SOURCE_CANCELLATION_ACCOUNT,
    isKyoto: true,
  },
  [AccountType.NON_COMPLIANCE_CANCELLATION_ACCOUNT]: {
    label: 'KP - Non-compliance cancellation account',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.NON_COMPLIANCE_CANCELLATION_ACCOUNT,
    isKyoto: true,
  },
  [AccountType.UK_SURRENDER_ACCOUNT]: {
    label: 'ETS - UK Surrender Account',
    registryAccountType: RegistryAccountType.UK_SURRENDER_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.UK_DELETION_ACCOUNT]: {
    label: 'ETS - UK Deletion Account',
    registryAccountType: RegistryAccountType.UK_DELETION_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.UK_TOTAL_QUANTITY_ACCOUNT]: {
    label: 'ETS - UK Total Quantity Account',
    registryAccountType: RegistryAccountType.UK_TOTAL_QUANTITY_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.UK_AVIATION_TOTAL_QUANTITY_ACCOUNT]: {
    label: 'ETS - UK Aviation Total Quantity Account',
    registryAccountType: RegistryAccountType.UK_AVIATION_TOTAL_QUANTITY_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.UK_AUCTION_ACCOUNT]: {
    label: 'ETS - UK Auction Account',
    registryAccountType: RegistryAccountType.UK_AUCTION_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.UK_ALLOCATION_ACCOUNT]: {
    label: 'ETS - UK Allocation Account',
    registryAccountType: RegistryAccountType.UK_ALLOCATION_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT]: {
    label: 'ETS - UK New Entrants Reserve Account',
    registryAccountType: RegistryAccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.UK_MARKET_STABILITY_MECHANISM_ACCOUNT]: {
    label: 'ETS - UK Market Stability Mechanism Account',
    registryAccountType:
      RegistryAccountType.UK_MARKET_STABILITY_MECHANISM_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.UK_GENERAL_HOLDING_ACCOUNT]: {
    label: 'ETS - UK General Holding Account',
    registryAccountType: RegistryAccountType.UK_GENERAL_HOLDING_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.NATIONAL_HOLDING_ACCOUNT]: {
    label: 'ETS - National holding account',
    registryAccountType: RegistryAccountType.NATIONAL_HOLDING_ACCOUNT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
  [AccountType.RETIREMENT_ACCOUNT]: {
    label: 'KP - Retirement account',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.RETIREMENT_ACCOUNT,
    isKyoto: true,
  },
  [AccountType.VOLUNTARY_CANCELLATION_ACCOUNT]: {
    label: 'KP - Voluntary cancellation account',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.VOLUNTARY_CANCELLATION_ACCOUNT,
    isKyoto: true,
  },
  [AccountType.MANDATORY_CANCELLATION_ACCOUNT]: {
    label: 'KP - Mandatory cancellation account',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.MANDATORY_CANCELLATION_ACCOUNT,
    isKyoto: true,
  },
  [AccountType.ARTICLE_3_7_TER_CANCELLATION_ACCOUNT]: {
    label: 'KP - Art37ter cancellation account',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.ARTICLE_3_7_TER_CANCELLATION_ACCOUNT,
    isKyoto: true,
  },
  [AccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT]: {
    label: 'KP - Ambition increase cancellation account',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT,
    isKyoto: true,
  },
  [AccountType.TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY]: {
    label: 'KP - tCER Replacement account for expiry',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY,
    isKyoto: true,
  },
  [AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY]: {
    label: 'KP - lCER Replacement account for expiry',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY,
    isKyoto: true,
  },
  [AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE]: {
    label: 'KP - lCER Replacement account for reversal of storage',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType:
      KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE,
    isKyoto: true,
  },
  [AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT]:
    {
      label:
        'KP - lCER Replacement account for non-submission of certification report',
      registryAccountType: RegistryAccountType.NONE,
      kyotoAccountType:
        KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT,
      isKyoto: true,
    },
  [AccountType.EXCESS_ISSUANCE_CANCELLATION_ACCOUNT]: {
    label: 'KP - Excess issuance cancellation account',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.EXCESS_ISSUANCE_CANCELLATION_ACCOUNT,
    isKyoto: true,
  },
  [AccountType.CCS_NET_REVERSAL_CANCELLATION_ACCOUNT]: {
    label: 'KP - CCS net reversal cancellation account',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.CCS_NET_REVERSAL_CANCELLATION_ACCOUNT,
    isKyoto: true,
  },
  [AccountType.CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT]:
    {
      label:
        'KP - CCS non-submission of verification report cancellation account',
      registryAccountType: RegistryAccountType.NONE,
      kyotoAccountType:
        KyotoAccountType.CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT,
      isKyoto: true,
    },
  [AccountType.PENDING_ACCOUNT]: {
    label: 'KP - Pending Account',
    registryAccountType: RegistryAccountType.NONE,
    kyotoAccountType: KyotoAccountType.PENDING_ACCOUNT,
    isKyoto: true,
  },
  [AccountType.AAU_DEPOSIT]: {
    label: 'ETS - AAU Deposit Account',
    registryAccountType: RegistryAccountType.AAU_DEPOSIT,
    kyotoAccountType: KyotoAccountType.PARTY_HOLDING_ACCOUNT,
  },
};
