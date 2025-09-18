import { createFeatureSelector, createSelector } from '@ngrx/store';
import { State } from 'src/app/reducers';
import { accountOpeningFeatureKey } from '../account-opening.reducer';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { selectAllCountries } from '@shared/shared.selector';
import {
  AccountHolderContact,
  AccountHolderContactInfo,
} from '@shared/model/account/account-holder-contact';
import { AccountOpeningState } from '../account-opening.model';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { selectAccountHolder } from '@account-opening/account-holder/account-holder.selector';
import { AccountType } from '@shared/model/account';

const selectAccountOpening = createFeatureSelector<State, AccountOpeningState>(
  accountOpeningFeatureKey
);

export const selectAccountHolderContact = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.accountHolderContact
);

export const selectPrimaryAccountHolderContact = createSelector(
  selectAccountOpening,
  (accountOpeningState) =>
    accountOpeningState.accountHolderContactInfo.primaryContact
);

export const selectAlternativeAccountHolderContact = createSelector(
  selectAccountOpening,
  (accountOpeningState) =>
    accountOpeningState.accountHolderContactInfo.alternativeContact
);

export const selectAccountHolderContactInfo = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.accountHolderContactInfo
);

export const selectAccountHolderContactView = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.accountHolderContactView
);

export const selectAccountHolderContactWizardCompleted = createSelector(
  selectAccountOpening,
  (accountOpeningState) => accountOpeningState.accountHolderContactsCompleted
);

export const selectAddressCountry = createSelector(
  selectAccountHolderContact,
  selectAllCountries,
  (
    accountHolderContact: AccountHolderContact,
    allCountries: IUkOfficialCountry[]
  ) => {
    if (accountHolderContact && allCountries) {
      return allCountries.find(
        (country) => country.key === accountHolderContact.address.country
      ).item[0].name;
    }
  }
);

export const selectSameHolderAddress = createSelector(
  selectAccountHolderContactInfo,
  (info, props) =>
    props.contactType === ContactType.PRIMARY
      ? info.isPrimaryAddressSameAsHolder
      : info.isAlternativeAddressSameAsHolder
);

export const selectAccountHolderAddress = createSelector(
  selectAccountHolder,
  (holder) => holder.address
);

export const selectPrimaryContactAddressCountry = createSelector(
  selectPrimaryAccountHolderContact,
  selectAllCountries,
  (
    primaryAccountHolderContact: AccountHolderContact,
    allCountries: IUkOfficialCountry[]
  ) => {
    if (primaryAccountHolderContact && allCountries) {
      return allCountries.find(
        (country) => country.key === primaryAccountHolderContact.address.country
      ).item[0].name;
    }
  }
);

export const selectAlternativeContactAddressCountry = createSelector(
  selectAlternativeAccountHolderContact,
  selectAllCountries,
  (
    alternativeContactAddressCountry: AccountHolderContact,
    allCountries: IUkOfficialCountry[]
  ) => {
    if (alternativeContactAddressCountry && allCountries) {
      return allCountries.find(
        (country) =>
          country.key === alternativeContactAddressCountry.address.country
      ).item[0].name;
    }
  }
);
