import { UkDate } from '@shared/model/uk-date';

export class AccountHolderContactChanged {
  details?: {
    firstName: string;
    lastName: string;
    aka: string;
    birthDate: UkDate;
    isOverEighteen?: boolean;
  };
  positionInCompany?: string;
  address?: {
    buildingAndStreet: string;
    buildingAndStreet2: string;
    buildingAndStreet3: string;
    townOrCity: string;
    stateOrProvince: string;
    country: string;
    postCode: string;
  };
  phoneNumber?: {
    countryCode1: string;
    phoneNumber1: string;
    countryCode2: string;
    phoneNumber2: string;
  };
  emailAddress?: {
    emailAddress: string;
    emailAddressConfirmation: string;
  };
}
