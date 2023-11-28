import { SharedState } from '@shared/shared.reducer';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { ErrorSummary } from '@shared/error-summary';
import { AbstractControl } from '@angular/forms';
import {
  Individual,
  Organisation,
} from '../../shared/model/account/account-holder';
import { AccountOpeningState } from '../account-opening.model';
import { AccountType } from '@shared/model/account';

export function aTestIndividual(): Individual {
  const individual = new Individual();
  individual.details = {
    name: 'Peter Parker',
    firstName: 'Peter',
    lastName: 'Parker',
    birthDate: {
      day: '1',
      month: '1',
      year: '1990',
    },
    countryOfBirth: 'US',
  };
  individual.address = {
    buildingAndStreet: 'Somewhere in new york',
    buildingAndStreet2: 'Where?',
    buildingAndStreet3: 'Here!',
    postCode: '1111',
    townOrCity: 'New York',
    stateOrProvince: 'New York State',
    country: 'US',
  };
  individual.phoneNumber = {
    countryCode1: 'US',
    phoneNumber1: '132323232323',
    countryCode2: 'US',
    phoneNumber2: '454564545645',
  };
  individual.emailAddress = {
    emailAddress: 'spidy@avengers.com',
    emailAddressConfirmation: '',
  };

  return individual;
}

export function aTestOrganisation(): Organisation {
  const organisation = new Organisation();
  organisation.details = {
    name: 'Stark Industries',
    registrationNumber: '1231231234',
    noRegistrationNumJustification: '',
  };
  organisation.address = {
    buildingAndStreet: 'Stark Tower',
    buildingAndStreet2: 'Where?',
    buildingAndStreet3: 'Here!',
    postCode: '1111',
    townOrCity: 'New York',
    stateOrProvince: 'New York State',
    country: 'US',
  };

  return organisation;
}

export function aTestAccountHolderStateWithIndividual(): Partial<AccountOpeningState> {
  return {
    accountType: AccountType.OPERATOR_HOLDING_ACCOUNT,
    accountHolder: aTestIndividual(),
    accountHolderCompleted: false,
  };
}

export function aTestAccountHolderStateWithOrganisation(): Partial<AccountOpeningState> {
  return {
    accountType: AccountType.OPERATOR_HOLDING_ACCOUNT,
    accountHolder: aTestOrganisation(),
    accountHolderCompleted: false,
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

export function checkOneFilledOtherDisabled(
  control: AbstractControl,
  alternativeControl: AbstractControl
) {
  return control.value && alternativeControl.disabled;
}
