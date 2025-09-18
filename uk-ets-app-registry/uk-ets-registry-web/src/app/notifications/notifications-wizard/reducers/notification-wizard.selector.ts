import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  notificationsWizardFeatureKey,
  NotificationsWizardState,
} from '@notifications/notifications-wizard/reducers/notifications-wizard.reducer';
import { NotificationsUtils } from '@shared/utils/notifications.utils';
import { UploadStatus } from '@shared/model/file';

const selectNotificationWizardState =
  createFeatureSelector<NotificationsWizardState>(
    notificationsWizardFeatureKey
  );

export const selectNotificationId = createSelector(
  selectNotificationWizardState,
  (state) => state?.notificationId
);

export const selectCurrentNotification = createSelector(
  selectNotificationWizardState,
  (state) => {
    return NotificationsUtils.convertNotificationTimeZone(
      state.notification,
      false
    );
  }
);

export const selectNewNotification = createSelector(
  selectNotificationWizardState,
  (state) => {
    if (state.datesConvertedToLocalTimezone) {
      return state.notificationNew;
    }
    return NotificationsUtils.convertNotificationTimeZone(
      state.notificationNew,
      false
    );
  }
);

export const selectNotificationRequest = createSelector(
  selectNotificationWizardState,
  (state) => state.notificationRequest
);

export const selectSubmittedRequestIdentifier = createSelector(
  selectNotificationWizardState,
  (state) => state.submittedNotificationId
);

export const selectNotificationDefinition = createSelector(
  selectNotificationWizardState,
  (state) => state.notificationDefinition
);

export const selectTentativeRecipients = createSelector(
  selectNotificationWizardState,
  (state) =>
    state.notificationId
      ? state.notification.tentativeRecipients
      : state.notificationDefinition ?
          state.notificationDefinition.tentativeRecipients ?
            state.notificationDefinition.tentativeRecipients
      : state.fileHeader ?
          state.fileHeader.tentativeRecipients
          : null  
      : null
);

export const selectUploadFileProgress = createSelector(
  selectNotificationWizardState,
  (state) => state.progress
);

export const selectUploadFileIsInProgress = createSelector(
  selectNotificationWizardState,
  (state) => state.status === UploadStatus.Started && state.progress >= 0
);

export const selectRecipientsEmailsFile = createSelector(
  selectNotificationWizardState,
  (state) => state.fileHeader
);

export const selectNotificationType = createSelector(
  selectNotificationWizardState,
  (state) => state.notificationNew?.type
);
