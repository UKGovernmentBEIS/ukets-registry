import { UserDefinedAccountParts } from '@shared/model/account';

export interface AddTrustedAccount {
  account: UserDefinedAccountParts;
  description: string;
}
