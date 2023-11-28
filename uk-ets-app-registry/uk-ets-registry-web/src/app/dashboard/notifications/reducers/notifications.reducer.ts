import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import * as NotificationsActions from '../actions/notifications.actions';
import { DashboardNotification } from '@registry-web/dashboard/notifications/model';

export const notificationsFeatureKey = 'notifications';

export interface NotificationsState {
  notifications: DashboardNotification[];
  notificationDetailsLoaded: boolean;
}

export const initialState: NotificationsState = {
  notifications: null,
  notificationDetailsLoaded: false,
};

const notificationsReducer = createReducer(
  initialState,
  mutableOn(
    NotificationsActions.retrieveNotificationsSuccess,
    (state, notifications) => {
      state.notifications = notifications.notifications;
      state.notificationDetailsLoaded = true;
    }
  )
);

export function reducer(state: NotificationsState | undefined, action: Action) {
  return notificationsReducer(state, action);
}
