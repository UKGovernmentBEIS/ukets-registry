import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { PageParameters } from '@shared/search/paginator/paginator.model';
import { SortParameters } from '@shared/search/sort/SortParameters';
import { ErrorDetail } from '@shared/error-summary';
import { AccountStatus, AccountType, Regulator } from '@shared/model/account';
import { Status } from '@shared/model/status';
import { ComplianceStatus } from '@account-shared/model';

export type AllocationStatus =
  | 'FULLY_ALLOCATED'
  | 'NOT_YET_ALLOCATED'
  | 'UNDER_ALLOCATED'
  | 'OVER_ALLOCATED';

export type AllocationWithhold = 'WITHHELD' | 'ALLOWED';

export interface AccountSearchCriteria {
  accountIdOrName: string;
  accountStatus: AccountStatus;
  accountType: AccountType;
  accountHolderName: string;
  complianceStatus: ComplianceStatus;
  permitOrMonitoringPlanIdentifier: string;
  authorizedRepresentativeUrid: string;
  regulatorType: string;
  excludedForYear: string;
  allocationStatus: string;
  allocationWithholdStatus: string;
  operatorId: string;
  imo: string;
}

export interface AccountSearchResult {
  accountId: number;
  fullAccountNo: string;
  accountName: string;
  accountType: AccountType;
  accountHolderName: string;
  accountStatus: AccountStatus;
  complianceStatus: ComplianceStatus;
  balance: number;
}

export interface FiltersDescriptor {
  searchByUrid: boolean;
  accountTypeOptions: Option[];
  accountStatusOptions: string[];
}

export interface SearchActionPayload {
  criteria: AccountSearchCriteria;
  pageParameters: PageParameters;
  sortParameters: SortParameters;
  potentialErrors: Map<any, ErrorDetail>;
  isReport?: boolean;
  loadPageParametersFromState?: boolean;
}

export const regulatorMap: Record<Regulator, string> = {
  [Regulator.OPRED]: 'OPRED',
  [Regulator.DAERA]: 'DAERA',
  [Regulator.EA]: 'EA',
  [Regulator.NRW]: 'NRW',
  [Regulator.SEPA]: 'SEPA',
};

export const allocationStatusMap: Record<AllocationStatus, string> = {
  NOT_YET_ALLOCATED: 'Not yet allocated',
  UNDER_ALLOCATED: 'Under-allocated',
  OVER_ALLOCATED: 'Over-allocated',
  FULLY_ALLOCATED: 'Fully-allocated',
};

export const allocationWithholdStatusMap: Record<AllocationWithhold, string> = {
  WITHHELD: 'Withheld',
  ALLOWED: 'Not withheld',
};

export const accountStatusMap: Record<AccountStatus, Status> = {
  OPEN: {
    color: 'green',
    label: 'Open',
    summary:
      'Registry Administrators and Authorised Representatives can access the account and perform actions. ' +
      'The account can receive units.',
  },
  ALL_TRANSACTIONS_RESTRICTED: {
    color: 'red',
    label: 'All transactions restricted',
    summary:
      'Registry Administrators cannot propose transactions. All incoming and outgoing transactions ' +
      'for the account are restricted.',
  },
  SOME_TRANSACTIONS_RESTRICTED: {
    color: 'red',
    label: 'Some transactions restricted',
    summary:
      'Registry Administrators can access the account and perform actions. The account can receive units. ' +
      'All outgoing transactions are restricted apart from Surrenders, Reverse allocation, Return excess allocation and ' +
      'Return of excess auction.',
  },
  SUSPENDED: {
    color: 'red',
    label: 'Suspended',
    summary:
      'Registry Administrators can access the account and perform actions. Authorised Representatives cannot access or ' +
      'view the account. The account cannot receive units.',
  },
  SUSPENDED_PARTIALLY: {
    color: 'red',
    label: 'Suspended partially',
    summary:
      'Registry Administrators can access the account and perform actions. Authorised Representatives cannot access or ' +
      'view the account. The account can still receive units.',
  },
  TRANSFER_PENDING: {
    color: 'yellow',
    label: 'Transfer pending',
    summary:
      'Registry Administrators and Authorised Representatives can access the account but cannot perform actions. ' +
      'Registry Administrators can propose transactions from the account. The account cannot receive units.',
  },
  CLOSURE_PENDING: {
    color: 'yellow',
    label: 'Closure pending',
    summary:
      'A request to close this account has been submitted. The account cannot receive or transfer units. ',
  },
  PROPOSED: {
    color: 'purple',
    label: 'Proposed',
  },
  CLOSED: {
    color: 'grey',
    label: 'Closed',
    summary:
      'The account is permanently closed and can only be viewed by Registry Administrators. ' +
      'The account cannot receive units.',
  },
  ALL_EXCEPT_CLOSED: { color: 'blue', label: 'All except closed' },
};
