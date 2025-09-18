import { createAction, props } from '@ngrx/store';
import {
  AircraftOperator,
  MaritimeOperator,
  Installation,
  Operator,
} from '@shared/model/account';
import { NavigationExtras, Params } from '@angular/router';
import { OperatorUpdate } from '@operator-update/model/operator-update';

export enum OperatorUpdateActionTypes {
  FETCH_OPERATOR_INFO = '[Operator Update Wizard] Fetch current operator info',
  SET_OPERATOR_INFO = '[Operator Update Wizard] Set current operator info',
  SET_NEW_OPERATOR_INFO = '[Operator Update Wizard] Set new operator info',
  CLEAR_OPERATOR_UPDATE_REQUEST = '[Operator Update Wizard] Clear Operator update request',
  CANCEL_OPERATOR_UPDATE_REQUEST = '[Operator Update Wizard] Cancel Operator update request',
  NAVIGATE_TO = '[Operator Update Wizard] Navigate To',
  CANCEL_CLICKED = '[Operator Update Wizard] Cancel clicked',
  SUBMIT_UPDATE_REQUEST = '[Operator Update Wizard] Submit update request',
  SUBMIT_UPDATE_REQUEST_SUCCESS = '[Operator Update Wizard] Submit update request success',
  FETCH_EXISTS_INSTALLATION_PERMIT_ID = '[Operator Update Wizard] - Check if exists Installation Permit ID',
  FETCH_EXISTS_MONITORING_PLAN = '[Operator Update Wizard] - Check if exists Aircraft Monitoring Plan ID',
  FETCH_EXISTS_MARITIME_IMO = '[Operator Update Wizard] - Check if exists Monitoring Plan and IMO',
  FETCH_EXISTS_MARITIME_IMO_SUCCESS = '[Operator Update Wizard] - Check if exists Monitoring Plan and IMO success',
  FETCH_EXISTS_MARITIME_IMO_FAILURE = '[Operator Update Wizard] - Check if exists Monitoring Plan and IMO failure',
}

export const navigateTo = createAction(
  OperatorUpdateActionTypes.NAVIGATE_TO,
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const fetchCurrentOperatorInfo = createAction(
  OperatorUpdateActionTypes.FETCH_OPERATOR_INFO,
  props<{ accountId: string }>()
);

export const setCurrentOperatorInfoSuccess = createAction(
  OperatorUpdateActionTypes.SET_OPERATOR_INFO,
  props<{ operator: Operator }>()
);

export const setNewOperatorInfoSuccess = createAction(
  OperatorUpdateActionTypes.SET_NEW_OPERATOR_INFO,
  props<{ operator: Operator }>()
);

export const clearOperatorUpdateRequest = createAction(
  OperatorUpdateActionTypes.CLEAR_OPERATOR_UPDATE_REQUEST
);

export const cancelOperatorUpdateRequest = createAction(
  OperatorUpdateActionTypes.CANCEL_OPERATOR_UPDATE_REQUEST
);

export const cancelClicked = createAction(
  OperatorUpdateActionTypes.CANCEL_CLICKED,
  props<{ route: string }>()
);

export const submitUpdateRequest = createAction(
  OperatorUpdateActionTypes.SUBMIT_UPDATE_REQUEST,
  props<{
    operatorUpdate: OperatorUpdate;
  }>()
);

export const submitUpdateRequestSuccess = createAction(
  OperatorUpdateActionTypes.SUBMIT_UPDATE_REQUEST_SUCCESS,
  props<{ requestId: string }>()
);

export const checkIfExistsInstallationPermitId = createAction(
  OperatorUpdateActionTypes.FETCH_EXISTS_INSTALLATION_PERMIT_ID,
  props<{
    operator: Installation;
  }>()
);

export const checkIfExistsAircraftMonitoringPlanId = createAction(
  OperatorUpdateActionTypes.FETCH_EXISTS_MONITORING_PLAN,
  props<{
    operator: AircraftOperator;
  }>()
);

export const checkIfExistsMaritimeImoAndMonitorinfPlan = createAction(
  OperatorUpdateActionTypes.FETCH_EXISTS_MARITIME_IMO,
  props<{
    operator: MaritimeOperator;
  }>()
);

export const checkIfExistsMaritimeImoAndMonitoringPlanSuccess = createAction(
  OperatorUpdateActionTypes.FETCH_EXISTS_MARITIME_IMO_SUCCESS,
  props<{
    operator: MaritimeOperator;
  }>()
);

export const checkIfExistsMaritimeImoAndMonitoringPlanFailure = createAction(
  OperatorUpdateActionTypes.FETCH_EXISTS_MARITIME_IMO_FAILURE,
  props<{
    errorSummaries: any;
  }>()
);
