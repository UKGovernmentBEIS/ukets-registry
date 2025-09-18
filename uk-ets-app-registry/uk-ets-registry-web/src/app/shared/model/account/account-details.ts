import { ComplianceStatus } from '@account-shared/model';
import { AccountType } from '@shared/model/account/account-type.enum';

export type AccountStatus =
  | 'OPEN'
  | 'ALL_TRANSACTIONS_RESTRICTED'
  | 'SOME_TRANSACTIONS_RESTRICTED'
  | 'SUSPENDED_PARTIALLY'
  | 'SUSPENDED'
  | 'TRANSFER_PENDING'
  | 'CLOSURE_PENDING'
  | 'PROPOSED'
  | 'CLOSED'
  | 'ALL_EXCEPT_CLOSED';

export class AccountDetails {
  name: string;
  publicAccountIdentifier: string;
  accountType: string;
  accountNumber: string;
  accountHolderName: string;
  accountHolderId: string;
  accountStatus: AccountStatus;
  complianceStatus: ComplianceStatus;
  openingDate: string;
  closingDate: string;
  closureReason: string;
  address: {
    buildingAndStreet: string;
    buildingAndStreet2: string;
    buildingAndStreet3: string;
    townOrCity: string;
    stateOrProvince: string;
    country: string;
    postCode: string;
  };
  accountTypeEnum?: AccountType;
  billingEmail1: string;
  billingEmail2: string;
  billingContactDetails: {
    contactName: string;
    phoneNumberCountryCode: string;
    phoneNumber: string;
    email: string;
    sopCustomerId: string;
  };
  salesContactDetails?: {
    phoneNumberCountryCode?: string;
    phoneNumber?: string;
    emailAddress?: {
      emailAddress: string;
      emailAddressConfirmation: string;
    };
  };
  excludedFromBilling?: boolean;
  excludedFromBillingRemarks?: string;
  accountDetailsSameBillingAddress?: boolean;
}
