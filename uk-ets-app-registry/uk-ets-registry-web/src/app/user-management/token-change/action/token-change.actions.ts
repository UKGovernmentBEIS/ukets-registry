import { createAction, props } from '@ngrx/store';

export const actionLoadData = createAction('[Token Change Action] Load data');

export const actionLoadDataSuccess = createAction(
  '[Token Change Action] Load success data success',
  props<{
    tokenDate: string;
  }>()
);

/**
 * Action for button "Continue" in "Page 1: Enter Reason".
 * Reducer stores the reason.
 * Effect dispatches the shared actions canGoBack & navigateTo.
 */
export const actionSubmitReason = createAction(
  '[Token Change Action] Submit reason',
  props<{
    reason: string;
  }>()
);

/**
 * Action for button "Submit" in "Page 2: Enter Code".
 * Effect calls the OTP validation service, as following:
 * On success dispatches the propose token change action.
 * On error dispatches the shared errors action.
 */
export const actionSubmitToken = createAction(
  '[Token Change Action] Submit one time password',
  props<{
    otp: string;
  }>()
);

/**
 * Submits the proposal to the backend API and navigates to the last screen of the wizard.
 * This action is dispatched from the effect.
 * This action is handled by the effect and the effect should dispatch the navigateTo shared action
 */
export const actionSubmitProposal = createAction(
  '[Token Change Action] Propose token change'
);

/**
 * Navigates to "Page 1: Enter Reason" page.
 */
export const actionNavigateToEnterReason = createAction(
  '[Token Change Action] Navigate to Enter Reason page'
);

/**
 * Navigates to "Page 2: Enter Code" page.
 */
export const actionNavigateToEnterCode = createAction(
  '[Token Change Action] Navigate to Enter Code page'
);

/**
 * Navigates to "Page 3: Verification" page.
 */
export const actionNavigateToVerification = createAction(
  '[Token Change Action] Navigate to Verification page',
  props<{
    submittedRequestIdentifier: string;
  }>()
);

export const actionValidateEmailToken = createAction(
  '[Token Change Action] Validate Email Token',
  props<{ token: string }>()
);

export const actionValidateEmailTokenSuccess = createAction(
  '[Token Change Action] Validate Email Token Success',
  props<{ token: string }>()
);

export const actionValidateEmailTokenFailure = createAction(
  '[Token Change Action] Validate Email Token Failure'
);
