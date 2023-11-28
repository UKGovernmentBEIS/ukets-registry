import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { AccountHolderDetailsWizardActions } from '@account-management/account/account-holder-details-wizard/actions';
import { Draft } from 'immer';
import {
  AccountHolderContactChanged,
  AccountHolderDetailsType,
  AccountHolderInfoChanged,
} from '@account-management/account/account-holder-details-wizard/model';

export const accountHolderDetailsWizardFeatureKey =
  'account-holder-details-wizard';

export interface AccountHolderDetailsWizardState {
  updateType: AccountHolderDetailsType;
  accountHolderInfoChanged: AccountHolderInfoChanged;
  accountHolderContactChanged: AccountHolderContactChanged;
  isPrimaryAddressSameAsHolder: boolean;
  isAlternativePrimaryAddressSameAsHolder: boolean;
  submittedRequestIdentifier: string;
}

export const initialState: AccountHolderDetailsWizardState = {
  updateType: null,
  accountHolderInfoChanged: null,
  accountHolderContactChanged: null,
  isPrimaryAddressSameAsHolder: null,
  isAlternativePrimaryAddressSameAsHolder: null,
  submittedRequestIdentifier: null,
};

const accountHolderDetailsWizardReducer = createReducer(
  initialState,
  mutableOn(
    AccountHolderDetailsWizardActions.setRequestUpdateType,
    (state: Draft<AccountHolderDetailsWizardState>, { updateType }) => {
      if (state.updateType !== updateType) {
        resetState(state);
      }
      state.updateType = updateType;
    }
  ),
  mutableOn(
    AccountHolderDetailsWizardActions.setAccountHolderDetails,
    (
      state: Draft<AccountHolderDetailsWizardState>,
      { accountHolderInfoChanged }
    ) => {
      if (!state.accountHolderInfoChanged) {
        state.accountHolderInfoChanged = {
          details: accountHolderInfoChanged.details,
        };
      } else {
        state.accountHolderInfoChanged.details =
          accountHolderInfoChanged.details;
      }
    }
  ),
  mutableOn(
    AccountHolderDetailsWizardActions.setAccountHolderAddress,
    (
      state: Draft<AccountHolderDetailsWizardState>,
      { accountHolderInfoChanged }
    ) => {
      state.accountHolderInfoChanged.address = accountHolderInfoChanged.address;
      state.accountHolderInfoChanged.emailAddress =
        accountHolderInfoChanged.emailAddress;
      state.accountHolderInfoChanged.phoneNumber =
        accountHolderInfoChanged.phoneNumber;
    }
  ),
  mutableOn(
    AccountHolderDetailsWizardActions.setAccountHolderContactDetails,
    (
      state: Draft<AccountHolderDetailsWizardState>,
      { accountHolderContactChanged }
    ) => {
      if (!state.accountHolderContactChanged) {
        state.accountHolderContactChanged = {
          details: accountHolderContactChanged.details,
        };
      } else {
        state.accountHolderContactChanged.details =
          accountHolderContactChanged.details;
      }
    }
  ),
  mutableOn(
    AccountHolderDetailsWizardActions.setAccountHolderContactWorkDetails,
    (
      state: Draft<AccountHolderDetailsWizardState>,
      { accountHolderContactChanged }
    ) => {
      state.accountHolderContactChanged.address =
        accountHolderContactChanged.address;
      state.accountHolderContactChanged.emailAddress =
        accountHolderContactChanged.emailAddress;
      state.accountHolderContactChanged.phoneNumber =
        accountHolderContactChanged.phoneNumber;
      state.accountHolderContactChanged.positionInCompany =
        accountHolderContactChanged.positionInCompany;
    }
  ),
  mutableOn(
    AccountHolderDetailsWizardActions.clearAccountHolderDetailsUpdateRequest,
    (state) => {
      resetState(state);
    }
  ),
  mutableOn(
    AccountHolderDetailsWizardActions.submitUpdateRequestSuccess,
    (state, { requestId }) => {
      state.submittedRequestIdentifier = requestId;
    }
  ),
  mutableOn(
    AccountHolderDetailsWizardActions.setSameAddressPrimaryContact,
    (state, { sameAddress }) => {
      state.isPrimaryAddressSameAsHolder = sameAddress;
    }
  ),
  mutableOn(
    AccountHolderDetailsWizardActions.setSameAddressAlternativePrimaryContact,
    (state, { sameAddress }) => {
      state.isAlternativePrimaryAddressSameAsHolder = sameAddress;
    }
  )
);

export function reducer(
  state: AccountHolderDetailsWizardState | undefined,
  action: Action
) {
  return accountHolderDetailsWizardReducer(state, action);
}

function resetState(state: AccountHolderDetailsWizardState) {
  state.updateType = initialState.updateType;
  state.accountHolderInfoChanged = initialState.accountHolderInfoChanged;
  state.accountHolderContactChanged = initialState.accountHolderContactChanged;
  state.isPrimaryAddressSameAsHolder =
    initialState.isPrimaryAddressSameAsHolder;
  state.isAlternativePrimaryAddressSameAsHolder =
    initialState.isAlternativePrimaryAddressSameAsHolder;
  state.submittedRequestIdentifier = initialState.submittedRequestIdentifier;
}
