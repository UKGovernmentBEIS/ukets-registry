import { UkDate } from '../uk-date';
import { ContactPhoneNumbers } from '@registry-web/shared/model/account/account-contacts.interface';

export class AccountHolderContact {
  id: number;
  new: boolean;
  details: {
    firstName: string;
    lastName: string;
    aka: string;
    birthDate: UkDate;
    isOverEighteen?: boolean;
  };
  positionInCompany: string;
  address: {
    buildingAndStreet: string;
    buildingAndStreet2: string;
    buildingAndStreet3: string;
    townOrCity: string;
    stateOrProvince: string;
    country: string;
    postCode: string;
  };
  phoneNumber: ContactPhoneNumbers;
  emailAddress: {
    emailAddress: string;
    emailAddressConfirmation: string;
  };
}

export interface AccountHolderContactInfo {
  primaryContact: AccountHolderContact;
  alternativeContact: AccountHolderContact;
  isPrimaryAddressSameAsHolder?: boolean;
  isAlternativeAddressSameAsHolder?: boolean;
}
