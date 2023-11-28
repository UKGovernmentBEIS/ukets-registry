import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { ForgotPasswordActions } from '../actions';

export const forgotPasswordFeatureKey = 'forgot-pasword';

export interface ForgotPasswordState {
  email: string;
  token: string;
}

export const initialState: ForgotPasswordState = {
  email: null,
  token: null
};

const forgotPasswordReducer = createReducer(
  initialState,
  mutableOn(
    ForgotPasswordActions.requestResetPasswordEmail,
    (state, { email }) => {
      state.email = email;
    }
  ),
  mutableOn(ForgotPasswordActions.validateTokenSuccess, (state, { token }) => {
    state.token = token;
  }),
  mutableOn(ForgotPasswordActions.resetPasswordSuccess, (state, { email }) => {
    state.email = email;
  })
);

export function reducer(
  state: ForgotPasswordState | undefined,
  action: Action
) {
  return forgotPasswordReducer(state, action);
}
