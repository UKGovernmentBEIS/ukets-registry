import { createAction, props } from '@ngrx/store';
import { AlertsModel } from '@registry-web/dashboard/alert-monitoring/model/alerts.model';

export enum AlertsActionsTypes {
  getAlertDetails = '[AlertsInfo] Retrieve all alerts',
  retrieveAlertSuccess = '[AlertsInfo] Retrieve alert succeeded',
  retrieveAlertError = '[AlertsInfo] Retrieve alert failed',
  fetchIsAdminFail = '[AlertsInfo] Fetch is admin Fail',
  retrieveAlertFailedNoAdmin = '[AlertsInfo] Failed to retrieve alert as this is no Regular administrator',
}

export const getAlerts = createAction(AlertsActionsTypes.getAlertDetails);

export const retrieveAlertSuccess = createAction(
  AlertsActionsTypes.retrieveAlertSuccess,
  props<{ alerts: AlertsModel }>()
);

export const retrieveAlertError = createAction(
  AlertsActionsTypes.retrieveAlertError,
  props<{ error?: any }>()
);

export const fetchIsAdminFail = createAction(
  AlertsActionsTypes.fetchIsAdminFail,
  props<{
    error: string;
  }>()
);

export const retrieveAlertFailedNoAdmin = createAction(
  AlertsActionsTypes.retrieveAlertFailedNoAdmin
);
