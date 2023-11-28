import { createAction, props } from '@ngrx/store';
import { KeycloakEventType } from 'keycloak-angular';

export enum TimeoutActions {
  TRIGGER_TIMEOUT_TIMER = '[Shared] Trigger timeout timer',
  SHOW_TIMEOUT_DIALOG = '[Shared] Show timeout dialog',
  HIDE_TIMEOUT_DIALOG = '[Shared] Hide timeout dialog',
}

export const showTimeoutDialog = createAction(
  TimeoutActions.SHOW_TIMEOUT_DIALOG
);

export const hideTimeoutDialog = createAction(
  TimeoutActions.HIDE_TIMEOUT_DIALOG,
  props<{ shouldLogout: boolean }>()
);

export const triggerTimeoutTimer = createAction(
  TimeoutActions.TRIGGER_TIMEOUT_TIMER,
  props<{ eventType: KeycloakEventType }>()
);
