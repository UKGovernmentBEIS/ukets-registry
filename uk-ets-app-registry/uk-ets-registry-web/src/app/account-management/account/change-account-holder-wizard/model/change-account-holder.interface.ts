import { AccountHolder, AccountHolderContact } from '@shared/model/account';

export enum ChangeAccountHolderActionType {
  ACCOUNT_HOLDER_CHANGE_TO_EXISTING_HOLDER = 'ACCOUNT_HOLDER_CHANGE_TO_EXISTING_HOLDER',
  ACCOUNT_HOLDER_CHANGE_TO_CREATED_HOLDER = 'ACCOUNT_HOLDER_CHANGE_TO_CREATED_HOLDER',
}

export interface ChangeAccountHolderRequest {
  accountIdentifier: string;
  accountHolderChangeActionType: ChangeAccountHolderActionType;
  acquiringAccountHolder: AccountHolder;
  acquiringAccountHolderContactInfo: AccountHolderContact;
  accountHolderDelete?: boolean;
}
