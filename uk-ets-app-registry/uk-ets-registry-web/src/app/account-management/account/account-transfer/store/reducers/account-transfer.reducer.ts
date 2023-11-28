import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { AccountTransferActions } from '@account-transfer/store/actions';
import { Draft } from 'immer';
import { AccountTransferType } from '@account-transfer/model';
import {
  AccountHolder,
  AccountHolderContactInfo,
  AccountHolderType,
} from '@shared/model/account';

export const accountTransferFeatureKey = 'account-transfer';

export interface AccountTransferState {
  updateType: AccountTransferType;
  acquiringAccountHolder: AccountHolder;
  acquiringAccountHolderContactInfo: AccountHolderContactInfo;
  submittedRequestIdentifier: string;
}

export const initialState: AccountTransferState = {
  updateType: null,
  acquiringAccountHolder: null,
  acquiringAccountHolderContactInfo: null,
  submittedRequestIdentifier: null,
};

const accountStatusReducer = createReducer(
  initialState,
  mutableOn(
    AccountTransferActions.setAccountTransferType,
    (state: Draft<AccountTransferState>, selectedAccountTransferType) => {
      if (
        state.updateType !==
        selectedAccountTransferType.selectedAccountTransferType
          .selectedUpdateType
      ) {
        resetState(state);
      }
      state.updateType =
        selectedAccountTransferType.selectedAccountTransferType.selectedUpdateType;
      if (
        selectedAccountTransferType.selectedAccountTransferType
          .selectedExistingAccountHolder
      ) {
        state.acquiringAccountHolder = {
          id: selectedAccountTransferType.selectedAccountTransferType
            .selectedExistingAccountHolder.identifier,
          type: null,
          details: null,
          address: null,
        };
      }
    }
  ),
  mutableOn(
    AccountTransferActions.setAcquiringAccountHolderDetails,
    (state: Draft<AccountTransferState>, { acquiringOrganisationDetails }) => {
      if (state.acquiringAccountHolder) {
        state.acquiringAccountHolder.details.name =
          acquiringOrganisationDetails.name;
        state.acquiringAccountHolder.details = acquiringOrganisationDetails;
      } else {
        state.acquiringAccountHolder = {
          id: null,
          details: {
            name: acquiringOrganisationDetails.name,
            registrationNumber: acquiringOrganisationDetails.registrationNumber,
            noRegistrationNumJustification:
              acquiringOrganisationDetails.noRegistrationNumJustification,
          },
          address: {
            buildingAndStreet: null,
            buildingAndStreet2: null,
            buildingAndStreet3: null,
            postCode: null,
            townOrCity: null,
            stateOrProvince: null,
            country: null,
          },
          type: AccountHolderType.ORGANISATION,
        };
      }
    }
  ),
  mutableOn(
    AccountTransferActions.setAcquiringAccountHolderAddress,
    (state: Draft<AccountTransferState>, { acquiringOrganisationAddress }) => {
      if (state.acquiringAccountHolder) {
        state.acquiringAccountHolder.address =
          acquiringOrganisationAddress.address;
      } else {
        state.acquiringAccountHolder = {
          id: null,
          details: {
            name: null,
            registrationNumber: null,
            noRegistrationNumJustification: null,
          },
          address: acquiringOrganisationAddress.address,
          type: AccountHolderType.ORGANISATION,
        };
      }
    }
  ),
  mutableOn(
    AccountTransferActions.setAcquiringAccountHolderPrimaryContactDetails,
    (
      state: Draft<AccountTransferState>,
      { acquiringAccountHolderContactDetails }
    ) => {
      if (!state.acquiringAccountHolderContactInfo) {
        state.acquiringAccountHolderContactInfo = {
          primaryContact: {
            id: null,
            new: true,
            details: acquiringAccountHolderContactDetails.details,
            positionInCompany: null,
            address: {
              buildingAndStreet: null,
              buildingAndStreet2: null,
              buildingAndStreet3: null,
              townOrCity: null,
              stateOrProvince: null,
              country: null,
              postCode: null,
            },
            phoneNumber: {
              countryCode1: null,
              phoneNumber1: null,
              countryCode2: null,
              phoneNumber2: null,
            },
            emailAddress: {
              emailAddress: null,
              emailAddressConfirmation: null,
            },
          },
          alternativeContact: null,
          isPrimaryAddressSameAsHolder: false,
          isAlternativeAddressSameAsHolder: false,
        };
      } else {
        state.acquiringAccountHolderContactInfo.primaryContact.details =
          acquiringAccountHolderContactDetails.details;
      }
    }
  ),
  mutableOn(
    AccountTransferActions.setAcquiringAccountHolderPrimaryContactWorkDetails,
    (
      state: Draft<AccountTransferState>,
      { acquiringAccountHolderContactWorkDetails }
    ) => {
      state.acquiringAccountHolderContactInfo.primaryContact.address =
        acquiringAccountHolderContactWorkDetails.address;
      state.acquiringAccountHolderContactInfo.primaryContact.emailAddress =
        acquiringAccountHolderContactWorkDetails.emailAddress;
      state.acquiringAccountHolderContactInfo.primaryContact.phoneNumber =
        acquiringAccountHolderContactWorkDetails.phoneNumber;
      state.acquiringAccountHolderContactInfo.primaryContact.positionInCompany =
        acquiringAccountHolderContactWorkDetails.positionInCompany;
    }
  ),
  mutableOn(
    AccountTransferActions.setPrimaryContactWorkAddressSameAsAccountHolderAddress,
    (state, { primaryContactWorkAddressSameAsAccountHolderAddress }) => {
      state.acquiringAccountHolderContactInfo.isPrimaryAddressSameAsHolder =
        primaryContactWorkAddressSameAsAccountHolderAddress;
    }
  ),
  mutableOn(
    AccountTransferActions.loadAcquiringAccountHolder,
    (state: Draft<AccountTransferState>, { accountHolder }) => {
      state.acquiringAccountHolder = accountHolder;
    }
  ),
  mutableOn(
    AccountTransferActions.loadAcquiringAccountHolderContacts,
    (state: Draft<AccountTransferState>, { accountHolderContactInfo }) => {
      state.acquiringAccountHolderContactInfo = accountHolderContactInfo;
    }
  ),
  mutableOn(
    AccountTransferActions.submitUpdateRequestSuccess,
    (state, { requestId }) => {
      state.submittedRequestIdentifier = requestId;
    }
  ),
  mutableOn(AccountTransferActions.clearAccountTransferRequest, (state) => {
    resetState(state);
  })
);

export function reducer(
  state: AccountTransferState | undefined,
  action: Action
) {
  return accountStatusReducer(state, action);
}

function resetState(state) {
  state.updateType = initialState.updateType;
  state.acquiringAccountHolder = initialState.acquiringAccountHolder;
  state.acquiringAccountHolderContactInfo =
    initialState.acquiringAccountHolderContactInfo;
  state.submittedRequestIdentifier = initialState.submittedRequestIdentifier;
}
