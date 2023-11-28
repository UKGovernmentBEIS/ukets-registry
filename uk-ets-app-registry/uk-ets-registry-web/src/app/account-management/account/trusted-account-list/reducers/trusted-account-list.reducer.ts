import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  DescriptionUpdateActionOptionModel,
  TrustedAccountListUpdateType,
} from '@trusted-account-list/model';
import { TrustedAccountListActions } from '@trusted-account-list/actions';
import { TrustedAccount, UserDefinedAccountParts } from '@shared/model/account';

export const trustedAccountListFeatureKey = 'trusted-account-list';

export type DescriptionUpdateActionState = Omit<
  DescriptionUpdateActionOptionModel,
  'enabled'
>;

export interface TrustedAccountListState {
  updateType: TrustedAccountListUpdateType;
  trustedAccountsEligibleForRemoval: TrustedAccount[];
  selectedTrustedAccountsForRemoval: TrustedAccount[];
  userDefinedTrustedAccountParts: UserDefinedAccountParts;
  userDefinedTrustedAccountDescription: string;
  submittedRequestIdentifier: string;
  descriptionUpdateActionState: DescriptionUpdateActionState;
  kyotoAccountType: boolean;
  isCancelPendingActivationLoading: boolean;
  cancelPendingActivationResult: { success: boolean };
}

export const initialState: TrustedAccountListState = {
  updateType: null,
  trustedAccountsEligibleForRemoval: [],
  selectedTrustedAccountsForRemoval: [],
  userDefinedTrustedAccountParts: null,
  userDefinedTrustedAccountDescription: null,
  submittedRequestIdentifier: null,
  descriptionUpdateActionState: null,
  kyotoAccountType: null,
  isCancelPendingActivationLoading: false,
  cancelPendingActivationResult: { success: null },
};

const trustedAccountListReducer = createReducer(
  initialState,
  mutableOn(
    TrustedAccountListActions.setRequestUpdateType,
    (state, { updateType }) => {
      state.updateType = updateType;
    }
  ),
  mutableOn(
    TrustedAccountListActions.fetchTrustedAccountsToRemoveSuccess,
    (state, { trustedAccounts }) => {
      state.trustedAccountsEligibleForRemoval = trustedAccounts;
    }
  ),
  mutableOn(
    TrustedAccountListActions.selectTrustedAccountsForRemovalSuccess,
    (state, { trustedAccountForRemoval }) => {
      state.selectedTrustedAccountsForRemoval = trustedAccountForRemoval;
    }
  ),
  mutableOn(
    TrustedAccountListActions.clearTrustedAccountListUpdateRequest,
    (state) => {
      resetState(state);
    }
  ),
  mutableOn(
    TrustedAccountListActions.setUserDefinedTrustedAccount,
    (
      state,
      { userDefinedTrustedAccountParts, userDefinedTrustedAccountDescription }
    ) => {
      state.userDefinedTrustedAccountParts = userDefinedTrustedAccountParts;
      state.userDefinedTrustedAccountDescription =
        userDefinedTrustedAccountDescription;
    }
  ),
  mutableOn(
    TrustedAccountListActions.submitUpdateRequestSuccess,
    (state, { requestId }) => {
      state.submittedRequestIdentifier = requestId;
    }
  ),
  mutableOn(TrustedAccountListActions.setDescriptionAction, (state, action) => {
    state.descriptionUpdateActionState = action.descriptionUpdateActionState;
  }),
  mutableOn(
    TrustedAccountListActions.setDescription,
    (state, { description }) => {
      state.userDefinedTrustedAccountDescription = description;
    }
  ),
  mutableOn(
    TrustedAccountListActions.setUserDefinedTrustedAccountSuccess,
    (state, { kyotoAccountType }) => {
      state.kyotoAccountType = kyotoAccountType;
    }
  ),
  mutableOn(TrustedAccountListActions.clearDescription, (state) => {
    resetState(state);
  }),
  //Cancel Pending Activation Actions
  mutableOn(
    TrustedAccountListActions.cancelPendingActivationRequested,
    (state) => {
      state.isCancelPendingActivationLoading = true;
      state.cancelPendingActivationResult = {
        success: initialState.cancelPendingActivationResult.success,
      };
    }
  ),
  mutableOn(
    TrustedAccountListActions.cancelPendingActivationRequestedSuccess,
    (state) => {
      state.isCancelPendingActivationLoading = false;
      state.cancelPendingActivationResult = { success: true };
    }
  ),
  mutableOn(
    TrustedAccountListActions.cancelPendingActivationRequestedFailure,
    (state) => {
      state.isCancelPendingActivationLoading = false;
      state.cancelPendingActivationResult = { success: false };
    }
  ),
  mutableOn(
    TrustedAccountListActions.hideCancelPendingActivationSuccessBanner,
    (state) => {
      state.isCancelPendingActivationLoading = false;
      state.cancelPendingActivationResult = { success: false };
    }
  )
);

export function reducer(
  state: TrustedAccountListState | undefined,
  action: Action
) {
  return trustedAccountListReducer(state, action);
}

function resetState(state) {
  state.updateType = initialState.updateType;
  state.trustedAccountsEligibleForRemoval =
    initialState.trustedAccountsEligibleForRemoval;
  state.selectedTrustedAccountsForRemoval =
    initialState.selectedTrustedAccountsForRemoval;
  state.userDefinedTrustedAccountParts =
    initialState.userDefinedTrustedAccountParts;
  state.userDefinedTrustedAccountDescription =
    initialState.userDefinedTrustedAccountDescription;
  state.submittedRequestIdentifier = initialState.submittedRequestIdentifier;
  state.submittedRequestIdentifier = initialState.submittedRequestIdentifier;
  state.descriptionUpdateActionState =
    initialState.descriptionUpdateActionState;
  state.description = initialState.userDefinedTrustedAccountDescription;
}
