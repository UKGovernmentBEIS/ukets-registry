import { ErrorDetail } from '@registry-web/shared/error-summary';
import {
  PageParameters,
  Pagination,
} from '@registry-web/shared/search/paginator';
import { SortParameters } from '@registry-web/shared/search/sort/SortParameters';
import { Status } from '@shared/model/status';

export interface TrustedAccount {
  /**
   * The ID of the trusted account.
   */
  id: number;

  /**
   * The full identifier of the trusted account.
   */
  accountFullIdentifier: string;

  /**
   * Signifies if the trusted account is under the same account holder as the host account.
   */
  underSameAccountHolder: boolean;

  /**
   * The trusted account description.
   */
  description: string;

  /**
   * The trusted account name.
   */
  name: string;

  /**
   * The trusted account status.
   */
  status: TrustedAccountStatus;

  /**
   * The planned activation date of the trusted account.
   */
  activationDate: string;

  /**
   * The planned activation time of the trusted account.
   */
  activationTime: string;

  /**
   * Indicates whether the account is Kyoto type
   */
  kyotoAccountType?: boolean;
}

export interface TrustedAccountList {
  pageParameters: PageParameters;
  pagination: Pagination;
  sortParameters: SortParameters;
  results: TrustedAccount[];
  criteria: TALSearchCriteria;
  hideCriteria: boolean;
}

export interface SearchActionPayload {
  criteria: TALSearchCriteria;
  pageParameters: PageParameters;
  sortParameters: SortParameters;
  potentialErrors: Map<any, ErrorDetail>;
}

export interface TALSearchCriteria {
  accountId: string;
  accountFullIdentifier: string;
  trustedAccountType: boolean;
  accountNameOrDescription: string;
}

export enum TrustedAccountStatus {
  /**
   * The trusted account has been proposed and its approval is pending.
   */
  PENDING_ADDITION_APPROVAL = 'PENDING_ADDITION_APPROVAL',
  /**
   * The trusted account has been approved and its activation is pending (delayed).
   */
  PENDING_ACTIVATION = 'PENDING_ACTIVATION',
  /**
   * The trusted account has been approved and activated.
   */
  ACTIVE = 'ACTIVE',
  /**
   * The trusted account has been marked for removal and its approval is pending.
   */
  PENDING_REMOVAL_APPROVAL = 'PENDING_REMOVAL_APPROVAL',
  /**
   * The trusted account has been rejected.
   */
  REJECTED = 'REJECTED',
}

export enum TrustedAccountAction {
  PENDING_ADDITION_APPROVAL = 'PENDING_ADDITION_APPROVAL',
  PENDING_REMOVAL_APPROVAL = 'PENDING_REMOVAL_APPROVAL',
}

export enum TrustedAccountPending {
  PENDING_ADDITION_APPROVAL = 'PENDING_ADDITION_APPROVAL',
  PENDING_REMOVAL_APPROVAL = 'PENDING_REMOVAL_APPROVAL',
  PENDING_ACTIVATION = 'PENDING_ACTIVATION',
}

export enum TrustedAccountType {
  /**
   * The trusted account has been added manually
   */
  MANUALLY_ADDED = 'Manually added',
  /**
   * The trusted account is under the same account holder
   */
  AUTOMATICALLY_TRUSTED = 'Automatically trusted',
}

export const trustedAccountStatusMap: Record<TrustedAccountStatus, Status> = {
  ACTIVE: { color: 'green', label: 'Active' },
  PENDING_ACTIVATION: { color: 'grey', label: 'Activation' },
  PENDING_ADDITION_APPROVAL: { color: 'yellow', label: 'Addition Approval' },
  PENDING_REMOVAL_APPROVAL: { color: 'yellow', label: 'Removal Approval' },
  REJECTED: { color: 'red', label: 'Rejected' },
};

export const trustedAccountActionMap: Record<TrustedAccountAction, Status> = {
  PENDING_ADDITION_APPROVAL: { color: 'yellow', label: 'Addition' },
  PENDING_REMOVAL_APPROVAL: { color: 'yellow', label: 'Removal' },
};

export const trustedAccountPendingMap: Record<TrustedAccountPending, Status> = {
  PENDING_ACTIVATION: { color: 'grey', label: 'Activation' },
  PENDING_ADDITION_APPROVAL: { color: 'yellow', label: 'Approval' },
  PENDING_REMOVAL_APPROVAL: { color: 'yellow', label: 'Approval' },
};

export const TRUSTED_ACCOUNTS_TYPE_OPTIONS = [
  {
    label: '',
    value: null,
  },
  {
    label: TrustedAccountType.AUTOMATICALLY_TRUSTED,
    value: true,
  },
  {
    label: TrustedAccountType.MANUALLY_ADDED,
    value: false,
  },
];
