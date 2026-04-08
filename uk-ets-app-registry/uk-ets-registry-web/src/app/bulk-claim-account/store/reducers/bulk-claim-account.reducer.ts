import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { BulkClaimAccountActions } from '@bulk-claim-account/store/actions';
import { WritableDraft } from 'immer/dist/internal';

export const bulkClaimAccountReducerFeatureKey = 'bulk-claim-account';

export interface BulkClaimAccountState {
  numberOfAffectedAccounts: number;
  succesfullySendInvitations: number;
  failedToSendInvitations: number;
}

const initialState: BulkClaimAccountState = getInitialState();

const bulkClaimAccountReducer = createReducer(
  initialState,
  mutableOn(
    BulkClaimAccountActions.countEligibleBulkClaimAccountsSuccess,
    (state, { numberOfAffectedAccounts }) => {
      state.numberOfAffectedAccounts = numberOfAffectedAccounts;
    }
  ),
  mutableOn(
    BulkClaimAccountActions.sendBulkClaimAccountSuccess,
    (state, { result }) => {
      // state.numberOfAffectedAccounts = result.total;
      state.succesfullySendInvitations = result.successful;
      state.failedToSendInvitations = result.failed;
    }
  ),
  mutableOn(BulkClaimAccountActions.clearBulkClaimAccountRequest, (state) => {
    resetState(state);
  })
);

export function reducer(state: BulkClaimAccountState, action: Action) {
  return bulkClaimAccountReducer(state, action);
}

function resetState(state: WritableDraft<BulkClaimAccountState>) {
  state.numberOfAffectedAccounts = getInitialState().numberOfAffectedAccounts;
  state.succesfullySendInvitations =
    getInitialState().succesfullySendInvitations;
  state.failedToSendInvitations = getInitialState().failedToSendInvitations;
}

function getInitialState(): BulkClaimAccountState {
  return {
    numberOfAffectedAccounts: 0,
    succesfullySendInvitations: 0,
    failedToSendInvitations: 0,
  };
}
