import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  clearNotificationsDetails,
  setNotificationsDetails,
  setNotificationsDetailsSuccess,
} from '@notifications/notifications-details/actions/notification-details.action';
import { Notification } from '@notifications/notifications-wizard/model';

export const notificationsDetailsFeatureKey = 'notificationsDetails';

export interface NotificationsDetailsState {
  notificationId: string;
  notificationDetails: Notification;
}

export const initialState: NotificationsDetailsState = {
  notificationId: null,
  notificationDetails: null,
};

const notificationsDetailsReducer = createReducer(
  initialState,
  mutableOn(setNotificationsDetails, (state, { notificationId }) => {
    state.notificationId = notificationId;
  }),
  mutableOn(
    setNotificationsDetailsSuccess,
    (state, { notificationDetails }) => {
      state.notificationDetails = notificationDetails;
    }
  ),
  mutableOn(clearNotificationsDetails, (state) => {
    resetState(state);
  })
);

export function reducer(
  state: NotificationsDetailsState | undefined,
  action: Action
) {
  return notificationsDetailsReducer(state, action);
}

function resetState(state) {
  state.notificationId = initialState.notificationId;
  state.notificationDetails = initialState.notificationDetails;
}
