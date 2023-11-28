import { Status } from '@shared/model/status';
import { AccountStatus } from '@shared/model/account';

export type TransactionStatus =
  | 'DELAYED'
  | 'COMPLETED'
  | 'AWAITING_APPROVAL'
  | 'REJECTED'
  | 'CANCELLED'
  | 'MANUALLY_CANCELLED'
  | 'ACCEPTED'
  | 'CHECKED_NO_DISCREPANCY'
  | 'CHECKED_DISCREPANCY'
  | 'STL_CHECKED_NO_DISCREPANCY'
  | 'STL_CHECKED_DISCREPANCY'
  | 'PROPOSED'
  | 'TERMINATED'
  | 'REVERSED'
  | 'FAILED'
  | 'CORRECTED';

export interface Transaction {
  id: string;
  type: string;
  units: {
    quantity: number;
    type: string;
  };
  transferringAccount: {
    title: string;
    ukRegistryIdentifier: string;
    ukRegistryFullIdentifier: string;
    externalAccount: boolean;
    userHasAccess: boolean;
    accountStatus: AccountStatus;
  };
  acquiringAccount: {
    title: string;
    ukRegistryIdentifier: string;
    ukRegistryFullIdentifier: string;
    externalAccount: boolean;
    userHasAccess: boolean;
    accountStatus: AccountStatus;
  };
  lastUpdated: string;
  status: TransactionStatus;
  runningBalance: {
    quantity: number;
    type: string;
  };
  reversedByIdentifier: string;
  reversesIdentifier: string;
}

export interface TransactionSearchCriteria {
  transactionId: string;
  transactionType: string;
  transactionStatus: TransactionStatus;
  transactionLastUpdateDateFrom: string;
  transactionLastUpdateDateTo: string;
  transferringAccountNumber: string;
  acquiringAccountNumber: string;
  acquiringAccountType: string;
  transferringAccountType: string;
  unitType: string;
  initiatorUserId: string;
  approverUserId: string;
  transactionalProposalDateFrom: string;
  transactionalProposalDateTo: string;
  reversed: boolean;
}

export const transactionStatusMap: Record<TransactionStatus, Status> = {
  AWAITING_APPROVAL: { color: 'yellow', label: 'Awaiting approval' },
  PROPOSED: { color: 'purple', label: 'Proposed' },
  CHECKED_NO_DISCREPANCY: { color: 'green', label: 'Checked no discrepancy' },
  CHECKED_DISCREPANCY: { color: 'yellow', label: 'Checked discrepancy' },
  COMPLETED: { color: 'green', label: 'Completed' },
  TERMINATED: { color: 'orange', label: 'Terminated' },
  REJECTED: { color: 'red', label: 'Rejected' },
  CANCELLED: { color: 'red', label: 'Cancelled' },
  MANUALLY_CANCELLED: { color: 'red', label: 'Manually Cancelled' },
  ACCEPTED: { color: 'green', label: 'Accepted' },
  STL_CHECKED_NO_DISCREPANCY: {
    color: 'green',
    label: 'STL checked no discrepancy',
  },
  STL_CHECKED_DISCREPANCY: {
    color: 'yellow',
    label: 'STL checked discrepancy',
  },
  REVERSED: { color: 'pink', label: 'Reversed ITL' },
  DELAYED: { color: 'yellow', label: 'Delayed' },
  FAILED: { color: 'red', label: 'Failed' },
  CORRECTED: { color: 'green', label: 'Corrected' },
};
