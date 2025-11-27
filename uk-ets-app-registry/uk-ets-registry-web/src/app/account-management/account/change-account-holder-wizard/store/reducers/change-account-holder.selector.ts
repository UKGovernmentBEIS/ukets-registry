import { selectAccount } from '@account-management/account/account-details/account.selector';
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { ContactType } from '@shared/model/account-holder-contact-type';
import {
  changeAccountHolderFeatureKey,
  ChangeAccountHolderWizardState,
} from '@change-account-holder-wizard/store/reducers';
import {
  AccountHolderContact,
  AccountHolderSelectionType,
} from '@shared/model/account';
import { selectAllCountries } from '@registry-web/shared/shared.selector';
import { IUkOfficialCountry } from '@registry-web/shared/countries/country.interface';

const selectChangeAccountHolderWizardState =
  createFeatureSelector<ChangeAccountHolderWizardState>(
    changeAccountHolderFeatureKey
  );

export const selectChangeAccountHolderType = createSelector(
  selectChangeAccountHolderWizardState,
  (changeAccountHolderState) =>
    changeAccountHolderState.acquiringAccountHolder.type
);

export const selectChangeAccountHolderSelectionType = createSelector(
  selectChangeAccountHolderWizardState,
  (changeAccountHolderState) =>
    changeAccountHolderState.accountHolderSelectionType
);

export const selectCurrentAccountHolder = createSelector(
  selectAccount,
  (account) => account.accountHolder
);

export const selectAccountHolderList = createSelector(
  selectChangeAccountHolderWizardState,
  (changeAccountHolderState) => changeAccountHolderState.accountHolderList
);

export const selectAcquiringAccountHolder = createSelector(
  selectChangeAccountHolderWizardState,
  (changeAccountHolderState) => changeAccountHolderState.acquiringAccountHolder
);

export const selectAcquiringAccountHolderAddress = createSelector(
  selectChangeAccountHolderWizardState,
  (changeAccountHolderState) =>
    changeAccountHolderState.acquiringAccountHolder.address
);

export const selectAcquiringAccountHolderContact = createSelector(
  selectChangeAccountHolderWizardState,
  (changeAccountHolderState) =>
    changeAccountHolderState.acquiringAccountHolderContact
);

export const selectCurrentAccountHolderAddress = createSelector(
  selectAccount,
  (account) => account?.accountHolder?.address
);

export const selectSamePrimaryContactAddress = createSelector(
  selectChangeAccountHolderWizardState,
  selectAccount,
  (state, info) =>
    state.isPrimaryAddressSameAsHolder
      ? state.isPrimaryAddressSameAsHolder
      : info?.accountHolderContactInfo?.isPrimaryAddressSameAsHolder
);

export const selectChangeAccountHolderExisting = createSelector(
  selectChangeAccountHolderWizardState,
  (changeAccountHolderState) => {
    return (
      changeAccountHolderState.accountHolderSelectionType ===
        AccountHolderSelectionType.FROM_LIST ||
      changeAccountHolderState.accountHolderSelectionType ===
        AccountHolderSelectionType.FROM_SEARCH
    );
  }
);

export const selectChangeAccountHolderWizardCompleted = createSelector(
  selectChangeAccountHolderWizardState,
  (changeAccountHolderState) => changeAccountHolderState.accountHolderCompleted
);

export const selectAcquiringAccountHolderContactAddressCountry = createSelector(
  selectAcquiringAccountHolderContact,
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

// export const selectSameAlternativePrimaryContactAddress = createSelector(
//   selectChangeAccountHolderWizardState,
//   selectAccount,
//   (state, info) =>
//     state.isAlternativePrimaryAddressSameAsHolder
//       ? state.isAlternativePrimaryAddressSameAsHolder
//       : info?.accountHolderContactInfo?.isAlternativeAddressSameAsHolder
// );

export const selectContactType = createSelector(
  selectChangeAccountHolderWizardState,
  (state) => {
    return ContactType.PRIMARY;
    // if (
    //   state.updateType ===
    //     AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE ||
    //   state.updateType ===
    //     AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE ||
    //   state.updateType ===
    //     AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD
    // ) {
    //   return ContactType.ALTERNATIVE;
    // } else {
    //   return ContactType.PRIMARY;
    // }
  }
);

export const selectCurrentAccountHolderContact = createSelector(
  selectAccount,
  selectContactType,
  (account, contactType) => {
    return contactType === ContactType.PRIMARY
      ? account?.accountHolderContactInfo?.primaryContact
      : account?.accountHolderContactInfo?.alternativeContact;
  }
);

export const selectCurrentPrimaryAccountHolderContact = createSelector(
  selectAccount,
  (account) => account?.accountHolderContactInfo?.primaryContact
);

export const selectCurrentAlternativePrimaryAccountHolderContact =
  createSelector(
    selectAccount,
    (account) => account?.accountHolderContactInfo?.alternativeContact
  );

function getAccountHolderContactInfo(
  currentAHContact,
  accountHolderContactChanged
) {
  if (currentAHContact || accountHolderContactChanged) {
    return {
      id: null,
      new: null,
      details: accountHolderContactChanged?.details
        ? accountHolderContactChanged.details
        : { ...currentAHContact?.details, isOverEighteen: true },
      positionInCompany: accountHolderContactChanged?.positionInCompany
        ? accountHolderContactChanged.positionInCompany
        : currentAHContact?.positionInCompany,
      address: accountHolderContactChanged?.address
        ? accountHolderContactChanged.address
        : currentAHContact?.address,
      phoneNumber: accountHolderContactChanged?.phoneNumber
        ? accountHolderContactChanged.phoneNumber
        : currentAHContact?.phoneNumber,
      emailAddress: accountHolderContactChanged?.emailAddress
        ? accountHolderContactChanged.emailAddress
        : currentAHContact?.emailAddress,
    };
  } else {
    return null;
  }
}
