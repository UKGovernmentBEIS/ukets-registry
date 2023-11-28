import { createAction, props } from '@ngrx/store';
import { NavigationExtras, Params } from '@angular/router';
import { Notification } from '@notifications/notifications-wizard/model';

export const navigateTo = createAction(
  '[Notifications-details] Navigate to',
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const clearNotificationsDetails = createAction(
  '[Notifications-details] Clear notification details'
);

export const setNotificationsDetails = createAction(
  '[Notifications-details] Set Notification details',
  props<{
    notificationId: string;
  }>()
);

export const setNotificationsDetailsSuccess = createAction(
  '[Notifications-details] Set Notification details success',
  props<{ notificationDetails: Notification }>()
);
