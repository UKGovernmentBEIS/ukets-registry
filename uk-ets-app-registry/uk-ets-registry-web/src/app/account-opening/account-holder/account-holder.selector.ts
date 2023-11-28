import { createFeatureSelector, createSelector } from '@ngrx/store';
import { State } from 'src/app/reducers';
import { accountOpeningFeatureKey } from '../account-opening.reducer';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { selectAllCountries } from '@shared/shared.selector';
import {
  AccountHolder,
  AccountHolderSelectionType,
  AccountHolderType,
  Organisation,
  Individual
} from '@shared/model/account/account-holder';
import { AccountOpeningState } from '../account-opening.model';

const selectAccountOpening = createFeatureSelector<State, AccountOpeningState>(
  accountOpeningFeatureKey
);

export const selectAccountHolder = createSelector(
  selectAccountOpening,
  accountOpeningState => accountOpeningState.accountHolder
);

export const selectAccountHolderList = createSelector(
  selectAccountOpening,
  accountOpeningState => accountOpeningState.accountHolderList
);

export const selectAccountHolderType = createSelector(
  selectAccountOpening,
  accountOpeningState => {
    if (accountOpeningState.accountHolder) {
      return accountOpeningState.accountHolder.type;
    } else {
      return null;
    }
  }
);

export const selectAccountHolderSelectionType = createSelector(
  selectAccountOpening,
  accountOpeningState => accountOpeningState.accountHolderSelectionType
);

export const selectAccountHolderExisting = createSelector(
  selectAccountOpening,
  accountOpeningState => {
    return (
      accountOpeningState.accountHolderSelectionType ===
        AccountHolderSelectionType.FROM_LIST ||
      accountOpeningState.accountHolderSelectionType ===
        AccountHolderSelectionType.FROM_SEARCH
    );
  }
);

export const selectAccountHolderWizardCompleted = createSelector(
  selectAccountOpening,
  accountOpeningState => accountOpeningState.accountHolderCompleted
);

export const selectAddressCountry = createSelector(
  selectAccountHolder,
  selectAccountHolderType,
  selectAllCountries,
  (
    accountHolder: AccountHolder,
    accountHolderType: AccountHolderType,
    allCountries: IUkOfficialCountry[]
  ) => {
    if (accountHolder && allCountries) {
      if (accountHolderType === AccountHolderType.INDIVIDUAL) {
        return allCountries.find(
          country =>
            country.key === (accountHolder as Individual).address.country
        ).item[0].name;
      } else {
        return allCountries.find(
          country =>
            country.key === (accountHolder as Organisation).address.country
        ).item[0].name;
      }
    }
  }
);

export const selectCountryOfBirth = createSelector(
  selectAccountHolder,
  selectAllCountries,
  (accountHolder: AccountHolder, allCountries: IUkOfficialCountry[]) => {
    if (accountHolder && allCountries) {
      return allCountries.find(
        country =>
          country.key === (accountHolder as Individual).details.countryOfBirth
      ).item[0].name;
    }
  }
);
