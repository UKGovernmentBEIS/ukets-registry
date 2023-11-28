import { UkDate } from '../uk-date';

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
  phoneNumber: {
    countryCode1: string;
    phoneNumber1: string;
    countryCode2: string;
    phoneNumber2: string;
  };
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
