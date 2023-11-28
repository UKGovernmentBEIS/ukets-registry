import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { TalTransactionRulesActions } from '@tal-transaction-rules/actions';
import { TrustedAccountListRules } from '@shared/model/account';

export const talTransactionRulesFeatureKey = 'tal-transaction-rules';

export interface TalTransactionRulesState {
  currentRules: TrustedAccountListRules;
  newRules: TrustedAccountListRules;
  submittedRequestIdentifier: string;
}

export const initialState: TalTransactionRulesState = {
  currentRules: null,
  newRules: null,
  submittedRequestIdentifier: null
};

const talTransactionRulesReducer = createReducer(
  initialState,
  mutableOn(
    TalTransactionRulesActions.setCurrentRulesSuccess,
    (state, { currentRules }) => {
      state.currentRules = currentRules;
    }
  ),
  mutableOn(TalTransactionRulesActions.setNewRules, (state, { newRules }) => {
    state.newRules = {
      ...state.newRules,
      ...newRules
    };
  }),
  mutableOn(
    TalTransactionRulesActions.submitUpdateRequestSuccess,
    (state, { requestId }) => {
      state.submittedRequestIdentifier = requestId;
    }
  ),
  mutableOn(
    TalTransactionRulesActions.clearTalTransactionRulesUpdateRequest,
    state => {
      resetState(state);
    }
  )
);

export function reducer(
  state: TalTransactionRulesState | undefined,
  action: Action
) {
  return talTransactionRulesReducer(state, action);
}

function resetState(state) {
  state.currentRules = initialState.currentRules;
  state.newRules = initialState.newRules;
  state.submittedRequestIdentifier = initialState.submittedRequestIdentifier;
}
