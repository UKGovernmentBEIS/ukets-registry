import { createAction, props } from '@ngrx/store';
import { SharedActionTypes } from '@shared/shared.action';

export enum RecoveryMethodsActionTypes {
  HIDE_RECOVERY_NOTIFICATION_PAGE = '[Shared] Hide recovery notification page',
  HIDE_RECOVERY_NOTIFICATION_PAGE_SUCCESS = '[Shared] Hide recovery notification page success',
  HIDE_RECOVERY_NOTIFICATION_PAGE_ERROR = '[Shared] Hide recovery notification page error',
}

export const hideRecoveryNotificationPage = createAction(
  RecoveryMethodsActionTypes.HIDE_RECOVERY_NOTIFICATION_PAGE
);

export const hideRecoveryNotificationPageSuccess = createAction(
  RecoveryMethodsActionTypes.HIDE_RECOVERY_NOTIFICATION_PAGE_SUCCESS,
  props<{ hideRecoveryNotificationPage: boolean }>()
);

export const hideRecoveryNotificationPageError = createAction(
  RecoveryMethodsActionTypes.HIDE_RECOVERY_NOTIFICATION_PAGE_ERROR,
  props<{ error?: any }>()
);

export const retrieveUserRecoveryInfoSetSuccess = createAction(
  SharedActionTypes.RETRIEVE_USER_STATUS_SUCCESS,
  props<{
    phoneSet: boolean;
    emailSet: boolean;
    hideRecoveryMethodsNotification: boolean;
  }>()
);
