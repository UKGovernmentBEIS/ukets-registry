import { User } from '../shared/user/user';
import { RegistrationState } from './registration.reducer';
import { SharedState } from '../shared/shared.reducer';
import { IUkOfficialCountry } from '../shared/countries/country.interface';
import { ErrorSummary } from '../shared/error-summary/error-summary';

export function aTestUser(): User {
  const user = new User();

  user.emailAddress = 'spidy@avengers.com';
  user.emailAddressConfirmation = '';
  user.userId = '8989898 8989989 8988889 9999';
  user.username = 'spidy';
  user.password = '';
  user.firstName = 'Peter';
  user.lastName = 'Parker';
  user.alsoKnownAs = 'Spiderman';
  user.buildingAndStreet = 'Somewhere in new york';
  user.buildingAndStreetOptional = 'Where?';
  user.buildingAndStreetOptional2 = 'Here!';
  user.postCode = '1111';
  user.townOrCity = 'New York';
  user.stateOrProvince = 'New York State';
  user.country = 'US';
  user.birthDate = { day: '1', month: '2', year: '1980' };
  user.countryOfBirth = 'US';
  user.workMobileCountryCode = 'US';
  user.workMobilePhoneNumber = '132323232323';
  user.workAlternativeCountryCode = 'US';
  user.workAlternativePhoneNumber = '132323232324';
  user.noMobilePhoneNumberReason = '';
  user.workBuildingAndStreet = 'Somewhere in new york';
  user.workBuildingAndStreetOptional = 'Where?';
  user.workBuildingAndStreetOptional2 = 'Here!';
  user.workTownOrCity = 'New York';
  user.workStateOrProvince = 'New York State';
  user.workPostCode = '1111';
  user.workCountry = 'US';
  user.urid = 'GB12345678912';
  return user;
}

export function emptyTestRegistrationState(): Partial<RegistrationState> {
  return {
    user: new User(),
  };
}

export function aTestRegistrationState(): Partial<RegistrationState> {
  return {
    user: aTestUser(),
  };
}

export function aTestSharedState(): Partial<SharedState> {
  return {
    countries: aTestCountrySet(),
    errorSummary: anEmptyErrorSummary(),
  };
}

export function anEmptyErrorSummary(): ErrorSummary {
  return new ErrorSummary([]);
}

export function aTestCountrySet(): IUkOfficialCountry[] {
  return [
    {
      'index-entry-number': '191',
      'entry-number': '191',
      'entry-timestamp': '2016-04-05T13:23:05Z',
      key: 'US',
      item: [
        {
          country: 'US',
          'end-date': null,
          'official-name': 'The United States of America',
          name: 'United States',
          'citizen-names': 'United States citizen',
        },
      ],
      callingCode: '1',
    },
  ];
}

export function fullName(): string {
  return (
    aTestUser().firstName +
    ' ' +
    aTestUser().lastName +
    ' ' +
    aTestUser().alsoKnownAs
  );
}

export function escapeRegExp(s: string) {
  return s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

export function personalAddressRegExp(): string {
  return addressRegExp({
    buildingAndStreet: aTestUser().buildingAndStreet,
    buildingAndStreetOptional: aTestUser().buildingAndStreetOptional,
    buildingAndStreetOptional2: aTestUser().buildingAndStreetOptional,
  });
}

export function addressRegExp(address: {
  buildingAndStreet: string;
  buildingAndStreetOptional: string;
  buildingAndStreetOptional2: string;
}): string {
  return (
    `^\\s*${escapeRegExp(aTestUser().buildingAndStreet)}\\s*,` +
    `\\s*${escapeRegExp(aTestUser().buildingAndStreetOptional)}\\s*,` +
    `\\s*${escapeRegExp(aTestUser().buildingAndStreetOptional2)}\\s*`
  );
}

/**
 * TODO: Fix the method because of birth date
 */
// export function ageAndCountryOfBirth(): string {
//   return (
//     new Date().getFullYear() -
//     Number(aTestUser().yearOfBirth).valueOf() +
//     ' years old, ' +
//     countryNameFromCode(aTestUser().countryOfBirth)
//   );
// }

export function workMobileNumber(): string {
  return (
    aTestUser().workMobileCountryCode + ' ' + aTestUser().workMobilePhoneNumber
  );
}

export function emailAddress(): string {
  return aTestUser().emailAddress;
}

export function workAddressRegExp(): string {
  return addressRegExp({
    buildingAndStreet: aTestUser().workBuildingAndStreet,
    buildingAndStreetOptional: aTestUser().workBuildingAndStreetOptional,
    buildingAndStreetOptional2: aTestUser().workBuildingAndStreetOptional2,
  });
}

export function personalBuildingAndStreet(): string {
  return aTestUser().buildingAndStreet;
}

export function personalBuildingAndStreetOptional(): string {
  return aTestUser().buildingAndStreetOptional;
}

export function personalBuildingAndStreetOptional2(): string {
  return aTestUser().buildingAndStreetOptional2;
}

export function personalPostCode(): string {
  return aTestUser().postCode;
}

export function personalTownOrCity(): string {
  return aTestUser().townOrCity;
}

export function personalStateOrProvince(): string {
  return aTestUser().stateOrProvince;
}

export function personalCountry(): string {
  return aTestUser().country;
}

export function countryNameFromCode(countryCode: string) {
  return aTestCountrySet().find((country) => country.key === countryCode)
    .item[0].name;
}
