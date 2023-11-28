import {
  AccountHolder,
  AccountHolderContact,
  Organisation,
  OrganisationDetails,
} from '@registry-web/shared/model/account';
import { AccountHolderTypeAheadSearchResult } from '@registry-web/account-shared/model';

export * from './account-transfer-paths.model';

export type AccountTransferType =
  | 'ACCOUNT_TRANSFER_TO_EXISTING_HOLDER'
  | 'ACCOUNT_TRANSFER_TO_CREATED_HOLDER';

export interface SelectedAccountTransferType {
  selectedUpdateType: AccountTransferType;
  selectedExistingAccountHolder?: AccountHolderTypeAheadSearchResult;
}

export interface AccountTransferRequest {
  accountTransferType: AccountTransferType;
  accountIdentifier: string;
  existingAcquiringAccountHolderIdentifier?: number;
  acquiringAccountHolder?: AccountHolder;
  acquiringAccountHolderContactInfo?: AccountHolderContact;
}

export type AcquiringAccountHolderInfo = Pick<
  AccountTransferRequest,
  | 'existingAcquiringAccountHolderIdentifier'
  | 'acquiringAccountHolder'
  | 'acquiringAccountHolderContactInfo'
>;

export type AcquiringOrganisationDetails = Pick<
  OrganisationDetails,
  'name' | 'registrationNumber' | 'noRegistrationNumJustification'
>;

export type AcquiringOrganisationAddress = Pick<Organisation, 'address'>;

export type AcquiringAccountHolderContactDetails = Pick<
  AccountHolderContact,
  'details'
>;

export type AcquiringAccountHolderContactWorkDetails = Pick<
  AccountHolderContact,
  'positionInCompany' | 'address' | 'phoneNumber' | 'emailAddress'
>;
