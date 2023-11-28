import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  notificationsFeatureKey,
  NotificationsState,
} from '@registry-web/dashboard/notifications/reducers/notifications.reducer';

export * from './notifications.reducer';

export const selectNotificationsState =
  createFeatureSelector<NotificationsState>(notificationsFeatureKey);

export const selectNotifications = createSelector(
  selectNotificationsState,
  (state) => state.notifications
);
