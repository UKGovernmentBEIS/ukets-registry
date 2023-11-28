import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import * as AlertsActions from '../actions/alerts.actions';
import { KeycloakUser } from '@shared/user';
import { AlertsModel } from '@registry-web/dashboard/alert-monitoring/model/alerts.model';

export const alertsFeatureKey = 'alerts';

export interface AlertsState {
  userDetails: KeycloakUser;
  alerts: AlertsModel;
  lastLoginDate: string;
  unsuccessfulLoginAttempts: bigint;
  keycloakError?: string;
  alertDetailsLoaded: boolean;
}

export const initialState: AlertsState = {
  userDetails: null,
  alerts: null,
  unsuccessfulLoginAttempts: null,
  lastLoginDate: null,
  keycloakError: null,
  alertDetailsLoaded: false,
};

const alertsReducer = createReducer(
  initialState,
  mutableOn(AlertsActions.retrieveAlertSuccess, (state, alerts) => {
    state.alerts = alerts.alerts;
    state.alertDetailsLoaded = true;
  }),
  mutableOn(AlertsActions.fetchIsAdminFail, (state, response) => {
    state.keycloakError = response.error;
    state.alertDetailsLoaded = false;
  }),
  mutableOn(AlertsActions.retrieveAlertFailedNoAdmin, (state) => {
    state.alertDetailsLoaded = false;
  })
);

export function reducer(state: AlertsState | undefined, action: Action) {
  return alertsReducer(state, action);
}
