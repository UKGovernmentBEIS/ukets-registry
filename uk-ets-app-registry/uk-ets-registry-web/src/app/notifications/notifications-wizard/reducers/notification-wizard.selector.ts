import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  notificationsWizardFeatureKey,
  NotificationsWizardState,
} from '@notifications/notifications-wizard/reducers/notifications-wizard.reducer';
import { NotificationsUtils } from '@shared/utils/notifications.utils';

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
      : state.notificationDefinition.tentativeRecipients
);
