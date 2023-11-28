import { AccountAccessState, ARAccessRights } from '@shared/model/account';

export interface ArInAccount {
  accountName: string;
  accountIdentifier: number;
  accountHolderName: string;
  right: ARAccessRights;
  state: AccountAccessState;
  accountFullIdentifier: string;
}
