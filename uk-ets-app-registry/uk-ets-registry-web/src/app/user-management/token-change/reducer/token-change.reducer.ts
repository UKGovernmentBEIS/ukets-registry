import { createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  actionLoadDataSuccess,
  actionNavigateToEnterReason,
  actionNavigateToVerification,
  actionSubmitReason
} from '@user-management/token-change/action/token-change.actions';
import { Draft } from 'immer';

export const tokenChangeReducerFeatureKey = 'tokenChangeReducer';

export interface TokenChangeState {
  reason: string;
  submittedRequestIdentifier: string;
  tokenDate: string;
}

export const initialState: TokenChangeState = {
  reason: null,
  submittedRequestIdentifier: null,
  tokenDate: null
};

function resetState(state: Draft<TokenChangeState>) {
  state.reason = null;
  state.submittedRequestIdentifier = null;
  state.tokenDate = null;
}

export const reducer = createReducer(
  initialState,

  mutableOn(actionNavigateToEnterReason, state => {
    resetState(state);
  }),

  mutableOn(actionSubmitReason, (state, { reason }) => {
    state.reason = reason;
  }),

  mutableOn(
    actionNavigateToVerification,
    (state, { submittedRequestIdentifier }) => {
      state.submittedRequestIdentifier = submittedRequestIdentifier;
    }
  ),

  mutableOn(actionLoadDataSuccess, (state, { tokenDate }) => {
    state.tokenDate = tokenDate;
  })
);
