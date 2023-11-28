import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { AccountStatusActions } from '../actions';
import {
  AccountStatusActionOption,
  AccountStatusActionState
} from '@shared/model/account/account-status-action';

export const accountStatusFeatureKey = 'account-status';

export interface AccountStatusState {
  allowedAccountStatusActions: AccountStatusActionOption[];
  accountStatusAction: AccountStatusActionState;
  comment: string;
}

export const initialState: AccountStatusState = {
  allowedAccountStatusActions: null,
  accountStatusAction: null,
  comment: null
};

const accountStatusReducer = createReducer(
  initialState,
  mutableOn(
    AccountStatusActions.loadAllowedAccountStatusActions,
    (state, { changeAccountStatusActionTypes }) => {
      state.allowedAccountStatusActions = changeAccountStatusActionTypes;
    }
  ),
  mutableOn(
    AccountStatusActions.setSelectedAccountStatusAction,
    (state, { accountStatusAction }) => {
      state.accountStatusAction = accountStatusAction;
    }
  ),
  mutableOn(AccountStatusActions.setComment, (state, { comment }) => {
    state.comment = comment;
  }),
  mutableOn(AccountStatusActions.clearAccountStatus, state => {
    resetState(state);
  })
);

export function reducer(state: AccountStatusState | undefined, action: Action) {
  return accountStatusReducer(state, action);
}

function resetState(state) {
  state.allowedAccountStatusActions = initialState.allowedAccountStatusActions;
  state.accountStatusAction = initialState.accountStatusAction;
  state.comment = initialState.comment;
}
