import { AccountStatus } from '.';

export type AccountStatusAction =
  | 'RESTRICT_SOME_TRANSACTIONS'
  | 'REMOVE_RESTRICTIONS'
  | 'SUSPEND_PARTIALLY'
  | 'SUSPEND'
  | 'UNSUSPEND';

export interface AccountStatusActionOption {
  label: string;
  hint: string;
  value: AccountStatusAction;
  enabled: true;
  newStatus: AccountStatus;
  message?: string;
}

export interface AccountStatusRequest {
  accountId: string;
  status: AccountStatus;
  comment: string;
}

export type AccountStatusActionState = Omit<
  AccountStatusActionOption,
  'enabled'
>;
