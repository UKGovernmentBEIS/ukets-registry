import { createAction, props } from '@ngrx/store';
import { ResetPasswordRequest, ResetPasswordResponse } from '../../model';

export enum ForgotPasswordActionTypes {
  REQUEST_RESET_PASSWORD_EMAIL = '[Forgot Password] Request email with link to reset password',
  REQUEST_RESET_PASSWORD_EMAIL_SUCCESS = '[Forgot Password] Request email with link to reset password success',
  VALIDATE_TOKEN = '[Forgot Password] Validate Token',
  VALIDATE_TOKEN_SUCCESS = '[Forgot Password] Validate Token success',
  VALIDATE_TOKEN_FAILURE = '[Forgot Password] Validate Token failure',
  RESET_PASSWORD = '[Forgot Password] Reset Password',
  RESET_PASSWORD_SUCCESS = '[Forgot Password] Reset Password success',
  RESET_PASSWORD_FAILURE = '[Forgot Password] Reset Password failure'
}

export const requestResetPasswordEmail = createAction(
  ForgotPasswordActionTypes.REQUEST_RESET_PASSWORD_EMAIL,
  props<{ email: string }>()
);

export const requestResetPasswordEmailSuccess = createAction(
  ForgotPasswordActionTypes.REQUEST_RESET_PASSWORD_EMAIL_SUCCESS
);

export const validateToken = createAction(
  ForgotPasswordActionTypes.VALIDATE_TOKEN,
  props<{ token: string }>()
);

export const validateTokenSuccess = createAction(
  ForgotPasswordActionTypes.VALIDATE_TOKEN_SUCCESS,
  props<{ token: string }>()
);

export const validateTokenFailure = createAction(
  ForgotPasswordActionTypes.VALIDATE_TOKEN_FAILURE
);

export const resetPassword = createAction(
  ForgotPasswordActionTypes.RESET_PASSWORD,
  props<ResetPasswordRequest>()
);

export const resetPasswordSuccess = createAction(
  ForgotPasswordActionTypes.RESET_PASSWORD_SUCCESS,
  props<ResetPasswordResponse>()
);
