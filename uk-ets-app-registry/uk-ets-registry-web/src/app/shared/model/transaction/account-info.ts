import { TrustedAccountListRules } from '@shared/model/account';

export interface AccountInfo {
  identifier: number; // this field is not required for performing transactions. fullIdentifier is a natural key
  fullIdentifier: string; // GB-100-1002-1-84 -
  accountName: string; // Party Holding 1
  accountHolderName: string;
  accountType?: string;
  kyotoAccountType?: boolean; // indicates whether the account is Kyoto type
  isGovernment?: boolean; // indicates whether the account is Government
}

export interface AcquiringAccountInfo extends AccountInfo {
  trusted: boolean; // indicates whether the acquiringAccount Is Trusted
}

export interface ReversedAccountInfo extends AccountInfo {
  transferringAccountInfo: AccountInfo;
  acquiringAccountInfo: AccountInfo;
}
