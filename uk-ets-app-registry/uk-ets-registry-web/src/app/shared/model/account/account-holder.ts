import { UkDate } from '../uk-date';

export enum AccountHolderType {
  INDIVIDUAL = 'INDIVIDUAL',
  ORGANISATION = 'ORGANISATION',
  GOVERNMENT = 'GOVERNMENT',
}

export enum AccountHolderSelectionType {
  FROM_LIST = 'FROM_LIST',
  NEW = 'NEW',
  FROM_SEARCH = 'FROM_SEARCH',
}

export interface AccountHolder {
  id: number;
  type: AccountHolderType;
  details: IndividualDetails | OrganisationDetails | GovernmentDetails;
  address: AccountHolderAddress;
}
// TODO: remove this class and use a union for different account holders
export class Individual implements AccountHolder {
  id: number;
  type = AccountHolderType.INDIVIDUAL;
  details: IndividualDetails;
  address: AccountHolderAddress;
  // TODO: replace with PhoneInfo[] and in the backend
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

export class Government implements AccountHolder {
  id: number;
  type = AccountHolderType.GOVERNMENT;
  details: GovernmentDetails;
  address: AccountHolderAddress;
}

export class Organisation implements AccountHolder {
  id: number;
  type = AccountHolderType.ORGANISATION;
  details: OrganisationDetails;
  address: AccountHolderAddress;
}

export interface IndividualDetails {
  name: string;
  firstName: string;
  lastName: string;
  birthDate: UkDate;
  countryOfBirth: string;
  isOverEighteen?: boolean;
}

export interface IndividualContactDetails {
  address: AccountHolderAddress;
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

export interface OrganisationDetails {
  name: string;
  registrationNumber: string;
  noRegistrationNumJustification: string;
}

export interface AccountHolderAddress {
  buildingAndStreet: string;
  buildingAndStreet2: string;
  buildingAndStreet3: string;
  postCode: string;
  townOrCity: string;
  stateOrProvince: string;
  country: string;
}

export interface GovernmentDetails {
  name: string;
}

export enum RegistrationNumberType {
  REGISTRATION_NUMBER,
  REGISTRATION_NUMBER_REASON,
}

export function getAccountHolderFullName(details: IndividualDetails): string {
  return details?.firstName && details?.lastName
    ? `${details.firstName} ${details.lastName}`
    : details?.name;
}

export function getAccountHolderFirstAndMiddleNames(
  details: IndividualDetails
): string {
  return details?.firstName ? details.firstName : details?.name;
}
