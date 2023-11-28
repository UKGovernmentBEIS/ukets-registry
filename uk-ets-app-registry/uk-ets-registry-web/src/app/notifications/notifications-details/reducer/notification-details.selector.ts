import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  notificationsDetailsFeatureKey,
  NotificationsDetailsState,
} from '@notifications/notifications-details/reducer/notification-details.reducer';
import { NotificationsUtils } from '@shared/utils/notifications.utils';

const selectNotificationDetailsState =
  createFeatureSelector<NotificationsDetailsState>(
    notificationsDetailsFeatureKey
  );

export const selectNotificationId = createSelector(
  selectNotificationDetailsState,
  (state) => state.notificationId
);

export const selectNotificationDetails = createSelector(
  selectNotificationDetailsState,
  (state) => {
    return NotificationsUtils.convertNotificationTimeZone(
      state.notificationDetails,
      false
    );
  }
);
