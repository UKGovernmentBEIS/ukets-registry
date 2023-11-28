import { createAction, props } from '@ngrx/store';
import { NavigationExtras } from '@angular/router';
import { EmailChangeRequest } from '@email-change/model/email-change.model';

/**
 * This action is dispatched from every point we want to navigate to email change wizard.
 * The action should have in its props the user urid for whom the change email request will take place
 * and the go back info.
 * This action is handled from the effect which dispatches the actions [canGoBack, navigateTo].
 */
export const navigateToEmailChangeWizard = createAction(
  `[Email Change] Prepare wizard by setting the urid and the go back url`,
  props<{
    urid: string;
    caller: {
      route: string;
      extras?: NavigationExtras;
    };
  }>()
);

export const requestEmailChangeAction = createAction(
  '[Email Change Enter New Email form] request email change',
  props<{
    request: EmailChangeRequest;
  }>()
);

/**
 * This action is dispatched from the effect and after the email change has been requested successfully.
 * It is handled from the effect which dispatches two actions [canGoBack, navigateTo].
 * The canGoBack should be dispatched with null goBackRoute in order to prevent previous page navigation.
 */
export const navigateToVerificationPage = createAction(
  '[Email change Effect listens to submitOtpCode action] Navigate to verification page',
  props<{
    newEmail: string;
  }>()
);

/**
 * This action is dispatched from the resolver of the confirmation page while the page is being loaded.
 * It is handled from the effect which calls the backend service and dispatches
 * the loadConfirmation action with the result of the confirmation.
 */
export const confirmEmailChange = createAction(
  '[Email change confirmation resolver] Confirm the token',
  props<{
    token: string;
  }>()
);

/**
 * This action is returned from the effect and is handled by the reducer.
 */
export const loadConfirmation = createAction(
  `[Email change effect listens to confirmEmailChange action]
Load the confirmation response to store`,
  props<{
    urid: string;
    expired: boolean;
    requestId: string;
  }>()
);
