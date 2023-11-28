import {
  ForgotPasswordState,
  forgotPasswordFeatureKey
} from './forgot-password.reducer';
import { createFeatureSelector, createSelector } from '@ngrx/store';

export * from './forgot-password.reducer';

const selectForgotPasswordState = createFeatureSelector<ForgotPasswordState>(
  forgotPasswordFeatureKey
);

export const selectResetPasswordToken = createSelector(
  selectForgotPasswordState,
  state => state.token
);

export const selectResetPasswordEmail = createSelector(
  selectForgotPasswordState,
  state => state.email
);
