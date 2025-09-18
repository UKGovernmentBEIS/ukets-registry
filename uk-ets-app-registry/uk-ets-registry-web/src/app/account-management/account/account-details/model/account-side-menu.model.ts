export enum MenuItemEnum {
  OVERVIEW = 'Overview',
  ACCOUNT_DETAILS = 'Account details',
  ACCOUNT_HOLDER = 'Account Holder',
  ARS = 'Authorised representatives',
  HISTORY_AND_COMMENTS = 'History and comments',
  HOLDINGS = 'Holdings',
  TRANSACTIONS = 'Transactions',
  TRUSTED_ACCOUNTS = 'Trusted accounts',
  INSTALLATION_DETAILS = 'Installation details',
  AIRCRAFT_OPERATOR_DETAILS = 'Aircraft operator details',
  ALLOCATION = 'Allocation status for UK allowances',
  EMISSIONS_SURRENDERS = 'Emissions and Surrenders',
  NOTES = 'Notes',
  MARITIME_OPERATOR_DETAILS = 'Maritime operator details',
}

export const NO_OPERATOR_ITEMS = [
  MenuItemEnum.OVERVIEW,
  MenuItemEnum.ACCOUNT_DETAILS,
  MenuItemEnum.ACCOUNT_HOLDER,
  MenuItemEnum.ARS,
  MenuItemEnum.HISTORY_AND_COMMENTS,
  MenuItemEnum.HOLDINGS,
  MenuItemEnum.TRANSACTIONS,
  MenuItemEnum.TRUSTED_ACCOUNTS,
  MenuItemEnum.NOTES,
];

export const OHAI_ITEMS = [
  MenuItemEnum.OVERVIEW,
  MenuItemEnum.ACCOUNT_DETAILS,
  MenuItemEnum.ACCOUNT_HOLDER,
  MenuItemEnum.INSTALLATION_DETAILS,
  MenuItemEnum.ALLOCATION,
  MenuItemEnum.ARS,
  MenuItemEnum.EMISSIONS_SURRENDERS,
  MenuItemEnum.HISTORY_AND_COMMENTS,
  MenuItemEnum.HOLDINGS,
  MenuItemEnum.TRANSACTIONS,
  MenuItemEnum.TRUSTED_ACCOUNTS,
  MenuItemEnum.NOTES,
];

export const AOHA_ITEMS = [
  MenuItemEnum.OVERVIEW,
  MenuItemEnum.ACCOUNT_DETAILS,
  MenuItemEnum.ACCOUNT_HOLDER,
  MenuItemEnum.AIRCRAFT_OPERATOR_DETAILS,
  MenuItemEnum.ALLOCATION,
  MenuItemEnum.ARS,
  MenuItemEnum.EMISSIONS_SURRENDERS,
  MenuItemEnum.HISTORY_AND_COMMENTS,
  MenuItemEnum.HOLDINGS,
  MenuItemEnum.TRANSACTIONS,
  MenuItemEnum.TRUSTED_ACCOUNTS,
  MenuItemEnum.NOTES,
];

export const MOHA_ITEMS = [
  MenuItemEnum.OVERVIEW,
  MenuItemEnum.ACCOUNT_DETAILS,
  MenuItemEnum.ACCOUNT_HOLDER,
  MenuItemEnum.MARITIME_OPERATOR_DETAILS,
  MenuItemEnum.ARS,
  MenuItemEnum.EMISSIONS_SURRENDERS,
  MenuItemEnum.HISTORY_AND_COMMENTS,
  MenuItemEnum.HOLDINGS,
  MenuItemEnum.TRANSACTIONS,
  MenuItemEnum.TRUSTED_ACCOUNTS,
  MenuItemEnum.NOTES,
];