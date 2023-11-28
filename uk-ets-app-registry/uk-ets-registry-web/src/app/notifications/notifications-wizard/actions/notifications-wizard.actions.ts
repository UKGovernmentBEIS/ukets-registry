import { createAction, props } from '@ngrx/store';
import { NavigationExtras, Params } from '@angular/router';
import {
  Notification,
  NotificationContent,
  NotificationDefinition,
  NotificationScheduledDate,
  NotificationType,
} from '@notifications/notifications-wizard/model';
import { NotificationRequestEnum } from '@notifications/notifications-wizard/model/notification-request.enum';

export const navigateTo = createAction(
  '[Notifications] Navigate to',
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const clearNotificationsRequest = createAction(
  '[Notifications] Clear Request'
);

export const setNotificationsRequest = createAction(
  '[Notifications] Set Notification Request',
  props<{
    notificationRequest: NotificationRequestEnum;
    notificationId: string;
  }>()
);

export const setNotificationsInfo = createAction(
  '[Notifications] Set Notification Info',
  props<{
    notificationId: string;
  }>()
);

export const setNotificationsInfoSuccess = createAction(
  '[Notifications] Set Notification Info success',
  props<{ notification: Notification }>()
);

export const setGoBackPath = createAction(
  '[Notifications] Set Notification Go back path',
  props<{ path: string; skipLocationChange: boolean }>()
);

export const goBackToSelectTypeOrNotificationDetails = createAction(
  '[Notifications] Go Back To select type or notification details page'
);

export const cancelClicked = createAction(
  '[Notifications] Cancel clicked',
  props<{ route: string }>()
);

export const cancelNotificationRequest = createAction(
  '[Notifications] Cancel Request'
);

export const setRequestNotificationType = createAction(
  '[Notifications] Set Request Notification type',
  props<{ notificationType: NotificationType }>()
);

export const setNotificationDefinition = createAction(
  '[Notifications] Set Notification Definition',
  props<{ notificationDefinition: NotificationDefinition }>()
);

export const setNotificationsScheduledDate = createAction(
  '[Notifications] Set Notification Scheduled Date',
  props<{ notificationScheduledDate: NotificationScheduledDate }>()
);

export const setNotificationsContent = createAction(
  '[Notifications] Set Notification Content',
  props<{ notificationContent: NotificationContent }>()
);

export const submitRequest = createAction(
  '[Notifications] Submit request',
  props<{ notification: Notification; notificationId: string }>()
);

export const submitRequestSuccess = createAction(
  '[Notifications] Submit request success',
  props<{ requestId: string }>()
);
