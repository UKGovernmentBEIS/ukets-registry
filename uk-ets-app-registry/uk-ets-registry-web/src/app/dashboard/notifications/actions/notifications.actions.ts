import { createAction, props } from '@ngrx/store';
import { DashboardNotification } from '@registry-web/dashboard/notifications/model';

export const retrieveNotifications = createAction(
  '[NotificationsInfo] Retrieve all notifications'
);

export const retrieveNotificationsSuccess = createAction(
  '[NotificationsInfo] Retrieve notification succeeded',
  props<{ notifications: DashboardNotification[] }>()
);

export const retrieveNotificationsError = createAction(
  '[NotificationsInfo] Retrieve notification failed',
  props<{ error?: any }>()
);
