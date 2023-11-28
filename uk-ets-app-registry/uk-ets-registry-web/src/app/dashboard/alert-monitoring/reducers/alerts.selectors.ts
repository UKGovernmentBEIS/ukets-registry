import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  AlertsState,
  alertsFeatureKey,
} from '@registry-web/dashboard/alert-monitoring/reducers/alerts.reducer';

export * from './alerts.reducer';

export const selectAlertsState = createFeatureSelector<AlertsState>(
  alertsFeatureKey
);

export const selectAlerts = createSelector(
  selectAlertsState,
  (state) => state.alerts
);
