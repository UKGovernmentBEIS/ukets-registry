import { AccountHolder, AccountHolderContact } from '@shared/model/account';

export * from './change-account-holder-wizard-paths.model';

export interface ChangeAccountHolderRequest {
  accountIdentifier: string;
  acquiringAccountHolder: AccountHolder;
  acquiringAccountHolderContactInfo: AccountHolderContact;
}
