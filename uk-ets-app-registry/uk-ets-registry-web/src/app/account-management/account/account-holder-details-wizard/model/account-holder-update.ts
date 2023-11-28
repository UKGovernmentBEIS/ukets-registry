import { AccountHolder, AccountHolderContact } from '@shared/model/account';
import {
  AccountHolderContactChanged,
  AccountHolderDetailsType,
  AccountHolderInfoChanged,
} from '@account-management/account/account-holder-details-wizard/model';

export interface AccountHolderUpdate {
  accountIdentifier: string;
  accountHolderIdentifier: number;
  currentAccountHolder: AccountHolder | AccountHolderContact;
  accountHolderDiff: AccountHolderInfoChanged | AccountHolderContactChanged;
  updateType: AccountHolderDetailsType;
}
