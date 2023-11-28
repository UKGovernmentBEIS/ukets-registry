import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { navigateToPasswordChangeWizard } from '@password-change/action/password-change.actions';

export const passwordChangeFeatureKey = 'password-change';

export interface PasswordChangeState {
  email: string;
  newPassword: string;
}

export const initialState: PasswordChangeState = {
  email: null,
  newPassword: null
};

const passwordChangeReducer = createReducer(
  initialState,
  mutableOn(navigateToPasswordChangeWizard, (state, { email }) => {
    state.newPassword = null;
    state.email = email;
  })
);

export function reducer(
  state: PasswordChangeState | undefined,
  action: Action
) {
  return passwordChangeReducer(state, action);
}
