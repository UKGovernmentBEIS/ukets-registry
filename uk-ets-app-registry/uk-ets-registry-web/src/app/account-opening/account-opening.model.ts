import {
  AccountDetails,
  AccountHolder,
  AccountHolderContact,
  AccountHolderContactInfo,
  AccountHolderSelectionType,
  AccountType,
  AuthorisedRepresentative,
  Installation,
  Operator,
} from '@shared/model/account';
import { TrustedAccountList } from './trusted-account-list/trusted-account-list';
import { RequestType } from '@registry-web/task-management/model';

export enum ViewOrCheck {
  VIEW,
  CHECK,
}

export interface AccountOpeningState {
  accountType: AccountType;
  accountHolder: AccountHolder;
  accountHolderList: AccountHolder[];
  accountHolderSelectionType: AccountHolderSelectionType;
  accountHolderContactInfo: AccountHolderContactInfo;
  accountHolderCompleted: boolean;
  accountHolderContact: AccountHolderContact;
  accountHolderContactView: boolean;
  accountHolderContactsCompleted: boolean;
  accountDetails: AccountDetails;
  accountDetailsCompleted: boolean;
  accountDetailsSameBillingAddress: boolean;
  trustedAccountList: TrustedAccountList;
  trustedAccountListCompleted: boolean;
  operator: Operator;
  installationToBeTransferred: Installation;
  operatorCompleted: boolean;
  authorisedRepresentativeIndex: number;
  currentAuthorisedRepresentative: AuthorisedRepresentative;
  authorisedRepresentatives: AuthorisedRepresentative[];
  fetchedAuthorisedRepresentatives: AuthorisedRepresentative[];
  authorisedRepresentativeViewOrCheck: ViewOrCheck;
  requestID: number;
  initialPermitId: string;
  minNumberOfARs: number;
  maxNumberOfARs: number;
  taskType: RequestType;
}
