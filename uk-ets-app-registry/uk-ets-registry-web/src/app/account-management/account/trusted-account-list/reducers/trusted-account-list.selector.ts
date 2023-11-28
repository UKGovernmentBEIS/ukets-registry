import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  trustedAccountListFeatureKey,
  TrustedAccountListState,
} from '@trusted-account-list/reducers/trusted-account-list.reducer';
import { TrustedAccountListUpdateType } from '@account-management/account/trusted-account-list/model';

const selectTrustedAccountListState =
  createFeatureSelector<TrustedAccountListState>(trustedAccountListFeatureKey);

export const selectUpdateType = createSelector(
  selectTrustedAccountListState,
  (state) => state.updateType
);

export const selectTrustedAccountsEligibleForRemoval = createSelector(
  selectTrustedAccountListState,
  (state) => state.trustedAccountsEligibleForRemoval
);

export const trustedAccountsForRemoval = createSelector(
  selectTrustedAccountListState,
  (state) => state.selectedTrustedAccountsForRemoval
);

export const calculateGoBackPathFromFinalPage = createSelector(
  selectTrustedAccountListState,
  (state) => {
    if (state.updateType === TrustedAccountListUpdateType.ADD) {
      return 'add-account';
    } else {
      return 'remove-account';
    }
  }
);

export const selectAddedTrustedAccount = createSelector(
  selectTrustedAccountListState,
  (state) => {
    return {
      userDefinedTrustedAccountParts: state.userDefinedTrustedAccountParts,
    };
  }
);

export const selectUserDefinedTrustedAccountFullIdentifier = createSelector(
  selectAddedTrustedAccount,
  (state) => {
    {
      if (state.userDefinedTrustedAccountParts == null) {
        return null;
      }
      let result: string;
      result =
        state.userDefinedTrustedAccountParts.userDefinedCountryCode +
        '-' +
        state.userDefinedTrustedAccountParts.userDefinedAccountType +
        '-' +
        state.userDefinedTrustedAccountParts.userDefinedAccountId;
      if (state.userDefinedTrustedAccountParts.userDefinedPeriod) {
        result += '-' + state.userDefinedTrustedAccountParts.userDefinedPeriod;
      }
      if (state.userDefinedTrustedAccountParts.userDefinedCheckDigits) {
        result +=
          '-' + state.userDefinedTrustedAccountParts.userDefinedCheckDigits;
      }
      return result;
    }
  }
);

export const selectAccountsToUpdate = createSelector(
  selectTrustedAccountListState,
  selectUserDefinedTrustedAccountFullIdentifier,
  (state, userDefinedTrustedAccountFullIdentifier) => {
    if (state.updateType === TrustedAccountListUpdateType.ADD) {
      return [
        {
          id: null,
          accountFullIdentifier: userDefinedTrustedAccountFullIdentifier,
          underSameAccountHolder: false,
          description: state.userDefinedTrustedAccountDescription,
          name: null,
          status: null,
          activationDate: null,
          activationTime: null,
        },
      ];
    } else {
      return state.selectedTrustedAccountsForRemoval;
    }
  }
);

export const selectSubmittedRequestIdentifier = createSelector(
  selectTrustedAccountListState,
  (state) => state.submittedRequestIdentifier
);

export const selectUserDefinedAccountParts = createSelector(
  selectTrustedAccountListState,
  (state) => state.userDefinedTrustedAccountParts
);

export const selectNewTrustedAccountDescription = createSelector(
  selectTrustedAccountListState,
  (state) => state.userDefinedTrustedAccountDescription
);

export const selectDescriptionUpdateAction = createSelector(
  selectTrustedAccountListState,
  (state) => state.descriptionUpdateActionState
);

export const selectIsKyotoAccountAction = createSelector(
  selectTrustedAccountListState,
  (state) => state.kyotoAccountType
);

export const isCancelPendingActivationSuccessAndNotLoading = createSelector(
  selectTrustedAccountListState,
  (state) =>
    state?.cancelPendingActivationResult?.success &&
    !state.isCancelPendingActivationLoading
);
