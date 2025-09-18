import { KeycloakUser } from './keycloak-user';
import { KnowsThePage } from '../../../../knows-the-page.po';

export class KeycloakUserBuilder {
  private _secret: string;

  private _username: string;
  private _enabled = true;
  private _email: string;
  private _firstName: string;
  private _lastName: string;
  private readonly _emailVerified = true;
  private readonly _attributes: {
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
    workEmailAddress: string[];
    workPhoneNumber: string[];
    workPostCode: string[];
    workTownOrCity: string[];
    workStateOrProvince: string[];
  };
  private readonly _credentials: [
    { type: string; value: string },
    {
      type: string;
      secretData: string;
      temporary: boolean;
      credentialData: string;
    },
  ];
  // private readonly _credentials: [{ type: string; value: string }];
  private readonly _realmRoles: string[];
  private readonly _getClientRoles: {
    account: string[];
    'uk-ets-registry-api': string[];
  };

  constructor() {
    this._emailVerified = true;
    this._email = 'dont@care.com';
    this._attributes = {
      urid: [],
      state: [],
      alsoKnownAs: [''],
      buildingAndStreet: ['Test Address 7'],
      buildingAndStreetOptional: ['Second address line'],
      buildingAndStreetOptional2: ['Third address line'],
      country: ['UK'],
      countryOfBirth: ['UK'],
      postCode: ['12345'],
      townOrCity: ['London'],
      stateOrProvince: ['London'],
      workBuildingAndStreet: ['Test work address 7'],
      workBuildingAndStreetOptional: ['Second work address line'],
      workBuildingAndStreetOptional2: ['Third work address line'],
      workCountry: ['UK'],
      workCountryCode: ['UK (44)'],
      workEmailAddress: ['dont@care.com'],
      workPhoneNumber: ['1434634996'],
      workTownOrCity: ['London'],
      workStateOrProvince: ['London'],
      workPostCode: ['12345'],
      yearOfBirth: ['1986'],
    };

    this._credentials = [
      {
        type: 'password',
        value: KnowsThePage.DEFAULT_SIGN_IN_PASSWORD,
      },
      {
        type: 'otp',
        secretData: 'this value is overridden by runtime secret generator',
        temporary: false,
        credentialData: `{"subType":"totp","digits":6,"counter":0,"period":30,"algorithm":"HmacSHA256"}`,
      },
    ];

    this._realmRoles = ['ets_user', 'uma_authorization'];

    this._getClientRoles = {
      account: ['manage-account'],
      'uk-ets-registry-api': [],
    };
  }

  build() {
    return new KeycloakUser(this);
  }

  get getUsername(): string {
    return this._username;
  }

  username(value: string) {
    this._username = value ? value : this._username;
    return this;
  }

  get enabled(): boolean {
    return this._enabled;
  }

  get getEmail(): string {
    return this._email;
  }

  email(value: string) {
    this._email = value ? value : this._email;
    return this;
  }

  get getFirstName(): string {
    return this._firstName;
  }

  firstName(value: string) {
    this._firstName = value ? value : this._firstName;
    return this;
  }

  get getLastName(): string {
    return this._lastName;
  }

  lastName(value: string) {
    this._lastName = value ? value : this._lastName;
    return this;
  }

  get emailVerified(): boolean {
    return this._emailVerified;
  }

  get getAttributes() {
    return this._attributes;
  }

  urid(value: string) {
    this._attributes.urid = value ? [value] : this._attributes.urid;
    return this;
  }

  state(value: string) {
    this._attributes.state = value ? [value] : this._attributes.state;
    return this;
  }

  personalDetails(
    line1: string = 'Test Address 7',
    line2: string = 'Second address line',
    line3: string = 'Third address line',
    country: string = 'UK',
    countryOfBirth: string = 'UK',
    postCode: string = '12345',
    townOrCity: string = 'London',
    stateOrProvince: string = 'London State',
    yearOfBirth: string = '1986'
  ) {
    this._attributes.buildingAndStreet = line1
      ? [line1]
      : this._attributes.buildingAndStreet;
    this._attributes.buildingAndStreetOptional = line2
      ? [line2]
      : this._attributes.buildingAndStreetOptional;
    this._attributes.buildingAndStreetOptional2 = line3
      ? [line3]
      : this._attributes.buildingAndStreetOptional2;
    this._attributes.country = country ? [country] : this._attributes.country;
    this._attributes.countryOfBirth = countryOfBirth
      ? [countryOfBirth]
      : this._attributes.countryOfBirth;
    this._attributes.postCode = postCode
      ? [postCode]
      : this._attributes.postCode;
    this._attributes.townOrCity = townOrCity
      ? [townOrCity]
      : this._attributes.townOrCity;
    this._attributes.stateOrProvince = stateOrProvince
      ? [stateOrProvince]
      : this._attributes.stateOrProvince;
    this._attributes.yearOfBirth = yearOfBirth
      ? [yearOfBirth]
      : this._attributes.yearOfBirth;
    return this;
  }

  workDetails(
    line1: string = 'Test Address 7',
    line2: string = 'Second address line',
    line3: string = 'Third address line',
    country: string = 'UK',
    countryCode: string = 'UK',
    emailAddress: string = 'dont@care.com',
    phoneNumber: string = '1434634996',
    postCode: string = '12345',
    townOrCity: string = 'London',
    stateOrProvince: string = 'London State'
  ) {
    this._attributes.workBuildingAndStreet = line1
      ? [line1]
      : this._attributes.workBuildingAndStreet;
    this._attributes.workBuildingAndStreetOptional = line2
      ? [line2]
      : this._attributes.workBuildingAndStreetOptional;
    this._attributes.workBuildingAndStreetOptional2 = line3
      ? [line3]
      : this._attributes.workBuildingAndStreetOptional2;
    this._attributes.workCountry = country
      ? [country]
      : this._attributes.workCountry;
    this._attributes.workCountryCode = countryCode
      ? [countryCode]
      : this._attributes.workCountryCode;
    this._attributes.workEmailAddress = emailAddress
      ? [emailAddress]
      : this._attributes.workEmailAddress;
    this._attributes.workPhoneNumber = phoneNumber
      ? [phoneNumber]
      : this._attributes.workPhoneNumber;
    this._attributes.workPostCode = postCode
      ? [postCode]
      : this._attributes.workPostCode;
    this._attributes.workTownOrCity = townOrCity
      ? [townOrCity]
      : this._attributes.workTownOrCity;
    this._attributes.workStateOrProvince = stateOrProvince
      ? [stateOrProvince]
      : this._attributes.workStateOrProvince;
    return this;
  }

  get getCredentials() {
    return this._credentials;
  }

  // first item of credentials
  password(value: string) {
    this._credentials[0].value = value ? value : this._credentials[0].value;
  }

  // second item of credentials
  otp(value: string) {
    this._credentials[1].secretData = value
      ? value
      : this._credentials[1].secretData;
    return this;
  }

  get realmRoles(): string[] {
    return this._realmRoles;
  }

  get getClientRoles(): {
    account: string[];
    'uk-ets-registry-api': string[];
  } {
    return this._getClientRoles;
  }

  clientRoles(value: string[]) {
    console.log('Client roles: ', value);
    this._getClientRoles['uk-ets-registry-api'] = value
      ? value
      : this._getClientRoles['uk-ets-registry-api'];
    console.log(
      'Client roles from object: ',
      this._getClientRoles['uk-ets-registry-api']
    );
    return this;
  }
}
