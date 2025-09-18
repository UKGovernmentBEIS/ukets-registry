import { KeycloakUserBuilder } from './keycloak-user.builder';

export class KeycloakUser {
  username: string;
  enabled = true;
  email: string;
  firstName: string;
  lastName: string;
  emailVerified = true;
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
    yearOfBirth: string[];
    state: string[];
    workBuildingAndStreet: string[];
    workBuildingAndStreetOptional: string[];
    workBuildingAndStreetOptional2: string[];
    workCountry: string[];
    workCountryCode: string[];
    workPhoneNumber: string[];
    workPostCode: string[];
    workTownOrCity: string[];
    workStateOrProvince: string[];
  };
  credentials: [
    { type: string; value: string },
    {
      type: string;
      secretData: string;
      temporary: boolean;
      credentialData: string;
    },
  ];

  realmRoles: string[];
  clientRoles: {
    account: string[];
    'uk-ets-registry-api': string[];
  };

  constructor(builder: KeycloakUserBuilder) {
    this.username = builder.getUsername;
    this.email = builder.getEmail;
    this.firstName = builder.getFirstName;
    this.lastName = builder.getLastName;
    this.attributes = builder.getAttributes;
    this.credentials = builder.getCredentials;
    this.realmRoles = builder.realmRoles;
    this.clientRoles = builder.getClientRoles;
  }
}
