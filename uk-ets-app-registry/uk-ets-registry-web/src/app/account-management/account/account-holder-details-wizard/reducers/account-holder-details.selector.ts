import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  accountHolderDetailsWizardFeatureKey,
  AccountHolderDetailsWizardState,
} from '@account-management/account/account-holder-details-wizard/reducers/account-holder-details.reducer';
import {
  AccountHolderDetailsType,
  AccountHolderDetailsTypeMap,
  AccountHolderDetailsWizardPathsModel,
} from '@account-management/account/account-holder-details-wizard/model';
import { selectAccount } from '@account-management/account/account-details/account.selector';
import { ContactType } from '@shared/model/account-holder-contact-type';
import { FormRadioOption } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import * as fromAuth from './../../../../auth/auth.selector';

const selectAccountHolderDetailsWizardState =
  createFeatureSelector<AccountHolderDetailsWizardState>(
    accountHolderDetailsWizardFeatureKey
  );

export const selectUpdateTypes = createSelector(
  selectAccount,
  fromAuth.isAuthorizedRepresentative,
  (account, isAuthorizedRepresentative) => {
    const options: FormRadioOption[] = [];
    if (
      !isAuthorizedRepresentative ||
      'TRADING_ACCOUNT' === account?.accountType
    ) {
      options.push({
        label: AccountHolderDetailsTypeMap.get(
          AccountHolderDetailsType.ACCOUNT_HOLDER_UPDATE_DETAILS
        ),
        value: AccountHolderDetailsType.ACCOUNT_HOLDER_UPDATE_DETAILS,
        enabled: true,
      });
    }
    options.push({
      label: AccountHolderDetailsTypeMap.get(
        AccountHolderDetailsType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS
      ),
      value: AccountHolderDetailsType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS,
      enabled: true,
    });
    if (account?.accountHolderContactInfo?.alternativeContact) {
      options.push({
        label: AccountHolderDetailsTypeMap.get(
          AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE
        ),
        value:
          AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE,
        enabled: true,
      });
      options.push({
        label: AccountHolderDetailsTypeMap.get(
          AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE
        ),
        value:
          AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE,
        enabled: true,
      });
    } else {
      options.push({
        label: AccountHolderDetailsTypeMap.get(
          AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD
        ),
        value:
          AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD,
        enabled: true,
      });
    }
    return options;
  }
);

export const selectCalculateGoBackPathFromCheckUpdateRequest = createSelector(
  selectAccountHolderDetailsWizardState,
  (state) => {
    switch (state.updateType) {
      case AccountHolderDetailsType.ACCOUNT_HOLDER_UPDATE_DETAILS:
        return AccountHolderDetailsWizardPathsModel.UPDATE_AH_ADDRESS;
      case AccountHolderDetailsType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS:
        return AccountHolderDetailsWizardPathsModel.UPDATE_PRIMARY_CONTACT_WORK;
      case AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE:
        return AccountHolderDetailsWizardPathsModel.UPDATE_ALTERNATIVE_PRIMARY_CONTACT_WORK;
      case AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE:
        return AccountHolderDetailsWizardPathsModel.SELECT_UPDATE_TYPE;
      case AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD:
        return AccountHolderDetailsWizardPathsModel.UPDATE_ALTERNATIVE_PRIMARY_CONTACT_WORK;
    }
  }
);

export const selectCurrentAccountHolder = createSelector(
  selectAccount,
  (account) => (account != null ? account.accountHolder : null)
);

export const selectCurrentAccountHolderAddress = createSelector(
  selectAccount,
  (account) => account?.accountHolder?.address
);

export const selectSamePrimaryContactAddress = createSelector(
  selectAccountHolderDetailsWizardState,
  selectAccount,
  (state, info) =>
    state.isPrimaryAddressSameAsHolder
      ? state.isPrimaryAddressSameAsHolder
      : info?.accountHolderContactInfo?.isPrimaryAddressSameAsHolder
);

export const selectSameAlternativePrimaryContactAddress = createSelector(
  selectAccountHolderDetailsWizardState,
  selectAccount,
  (state, info) =>
    state.isAlternativePrimaryAddressSameAsHolder
      ? state.isAlternativePrimaryAddressSameAsHolder
      : info?.accountHolderContactInfo?.isAlternativeAddressSameAsHolder
);

export const selectContactType = createSelector(
  selectAccountHolderDetailsWizardState,
  (state) => {
    if (
      state.updateType ===
        AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE ||
      state.updateType ===
        AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE ||
      state.updateType ===
        AccountHolderDetailsType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD
    ) {
      return ContactType.ALTERNATIVE;
    } else {
      return ContactType.PRIMARY;
    }
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

export const selectAccountHolderType = createSelector(
  selectAccount,
  (account) => (account != null ? account.accountHolder.type : null)
);

export const selectUpdateType = createSelector(
  selectAccountHolderDetailsWizardState,
  (state) => state.updateType
);

export const selectAccountHolderInfoChanged = createSelector(
  selectAccountHolderDetailsWizardState,
  (state) => state.accountHolderInfoChanged
);

export const selectAccountHolderContactChanged = createSelector(
  selectAccountHolderDetailsWizardState,
  (state) => state.accountHolderContactChanged
);

export const selectSubmittedRequestIdentifier = createSelector(
  selectAccountHolderDetailsWizardState,
  (state) => state.submittedRequestIdentifier
);

export const selectUpdatedAccountHolder = createSelector(
  selectCurrentAccountHolder,
  selectAccountHolderInfoChanged,
  (currentAH, accountHolderInfoChanged) => {
    if (currentAH) {
      const updateAccountHolder = {
        id: null,
        type: null,
        details: null,
        address: null,
        status: null,
      };
      // We use this dynamic assign because there are also other fields depending on the type (individual or organization)
      Object.keys(currentAH).forEach((r) => {
        if (r === 'details') {
          updateAccountHolder[r] = accountHolderInfoChanged?.details
            ? accountHolderInfoChanged.details
            : currentAH[r];
        } else if (r === 'address') {
          updateAccountHolder[r] = accountHolderInfoChanged?.address
            ? accountHolderInfoChanged.address
            : currentAH[r];
        } else if (r === 'phoneNumber') {
          updateAccountHolder[r] = accountHolderInfoChanged?.phoneNumber
            ? accountHolderInfoChanged.phoneNumber
            : currentAH[r];
        } else if (r === 'emailAddress') {
          updateAccountHolder[r] = accountHolderInfoChanged?.emailAddress
            ? accountHolderInfoChanged.emailAddress
            : currentAH[r];
        } else {
          updateAccountHolder[r] = currentAH[r];
        }
      });
      return updateAccountHolder;
    } else {
      return null;
    }
  }
);

export const selectUpdatedAccountHolderPrimaryContact = createSelector(
  selectCurrentPrimaryAccountHolderContact,
  selectAccountHolderContactChanged,
  (currentAHContact, accountHolderContactChanged) => {
    return getAccountHolderContactInfo(
      currentAHContact,
      accountHolderContactChanged
    );
  }
);

export const selectUpdatedAlternativeAccountHolderContact = createSelector(
  selectCurrentAlternativePrimaryAccountHolderContact,
  selectAccountHolderContactChanged,
  (currentAHContact, accountHolderContactChanged) => {
    return getAccountHolderContactInfo(
      currentAHContact,
      accountHolderContactChanged
    );
  }
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
