import { AccountHolder } from './account-holder';
import { AccountHolderContactInfo } from './account-holder-contact';
import { AccountDetails } from './account-details';
import { Operator } from './operator';
import { AuthorisedRepresentative } from './authorised-representative';
import { TrustedAccountListRules } from './trusted-account-list-rules';
import { AccountType } from './account-type.enum';
import { TrustedAccountList } from '@shared/model/account/trusted-account';
import { ArSubmittedUpdateRequest } from '@shared/model/account/ar-submitted-update-request';
import {
  MetsContact,
  RegistryContact,
} from '@registry-web/shared/model/account/account-contacts.interface';

export interface Account {
  identifier: number;
  accountType: AccountType;
  accountHolder: AccountHolder;
  accountHolderContactInfo: AccountHolderContactInfo;
  accountDetails: AccountDetails;
  operator: Operator;
  authorisedRepresentatives: AuthorisedRepresentative[];
  trustedAccountListRules: TrustedAccountListRules;
  complianceStatus: string;
  balance: number;
  unitType: string;
  governmentAccount: boolean;
  kyotoAccountType: boolean;
  trustedAccountList: TrustedAccountList;
  transactionsAllowed: boolean;
  canBeClosed: boolean;
  pendingARRequests: ArSubmittedUpdateRequest[];
  addedARs: number;
  removedARs: number;
  accountDetailsSameBillingAddress?: boolean;
  metsContacts?: MetsContact[];
  registryContacts?: RegistryContact[];
}
