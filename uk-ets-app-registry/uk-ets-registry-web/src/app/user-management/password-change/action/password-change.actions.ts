import { createAction, props } from '@ngrx/store';
import { PasswordChangeRequest } from '@password-change/model';

export const navigateToPasswordChangeWizard = createAction(
  `[Password Change] Prepare wizard by setting the email`,
  props<{
    email: string;
  }>()
);

export const requestPasswordChangeAction = createAction(
  '[Password Change] Request password change',
  props<{
    request: PasswordChangeRequest;
  }>()
);

export const navigateToConfirmationPage = createAction(
  '[Password change] Navigate to confirmation page'
);

export const successChangePasswordPage = createAction(
  '[Password change] Navigate to confirmation page success'
);
