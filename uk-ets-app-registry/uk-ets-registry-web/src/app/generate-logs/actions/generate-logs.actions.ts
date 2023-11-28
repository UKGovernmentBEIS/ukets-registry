import { createAction, props } from '@ngrx/store';
import {
  Exception,
  Log,
  ApiRequestCorrelation,
} from '@generate-logs/reducers/generate-logs.reducer';

export enum GenerateLogsActionTypes {
  RECEIVED_USER_ACTION = '[UI logs | DOM Listener] Received user action',
  GENERATED_LATEST_LOG_OBJECT = '[UI logs | Effects ] Generated latest log based on user action',
  POSTED_LOGS = '[UI logs | Effects ] Posted logs to the BACKEND service',
  POSTED_LOGS_ACCEPTED = '[UI logs | API] Received Accepted POSTED LOGS Response',
  POSTED_LOGS_FAILURE = '[UI logs | API] Received Failed POSTED LOGS Response',
  API_REQUEST_FAILURE = '[UI logs | API] An Http Error occurred',
  API_REQUEST_SUCCESS = '[UI logs | API] An Http Action was successful',
}

export interface Interaction {
  label: string;
  element: string;
  id: string;
  date: string;
  action_identifier: string;
  cause: string;
}

export interface Action {
  type: string;
  interaction_identifier: string;
}

export const receivedUserAction = createAction(
  GenerateLogsActionTypes.RECEIVED_USER_ACTION,
  props<{ interaction: Interaction }>()
);

export const apiRequestFailure = createAction(
  GenerateLogsActionTypes.API_REQUEST_FAILURE,
  props<{ exception: Exception; correlation: ApiRequestCorrelation }>()
);

export const apiRequestSuccess = createAction(
  GenerateLogsActionTypes.API_REQUEST_SUCCESS,
  props<{ correlation: ApiRequestCorrelation }>()
);

export const generatedLatestLogObject = createAction(
  GenerateLogsActionTypes.GENERATED_LATEST_LOG_OBJECT,
  props<{ log: Log }>()
);

export const postedLogs = createAction(
  GenerateLogsActionTypes.POSTED_LOGS,
  props<{ logs: Log[] }>()
);

export const postedLogsAccepted = createAction(
  GenerateLogsActionTypes.POSTED_LOGS_ACCEPTED,
  props<{ logs: Log[] }>()
);

export const postedLogsFailed = createAction(
  GenerateLogsActionTypes.POSTED_LOGS_FAILURE
);
