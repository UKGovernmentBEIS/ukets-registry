export interface KeycloakUser {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  eligibleForSpecificActions: boolean;
  userRoles: string[];
  attributes: {
    urid: string[];
    alsoKnownAs: string[];
    buildingAndStreet: string[];
    buildingAndStreetOptional: string[];
    buildingAndStreetOptional2: string[];
    country: string[];
    countryOfBirth: string[];
    postCode: string[];
    townOrCity: string[];
    stateOrProvince: string[];
    birthDate: string[];
    state: string[];
    workBuildingAndStreet: string[];
    workBuildingAndStreetOptional: string[];
    workBuildingAndStreetOptional2: string[];
    workCountry: string[];
    workCountryCode: string[];
    workEmailAddress: string[];
    workEmailAddressConfirmation: string[];
    workPhoneNumber: string[];
    workPostCode: string[];
    workTownOrCity: string[];
    workStateOrProvince: string[];
    lastLoginDate?: string[];
    memorablePhrase?: string;
  };
}
