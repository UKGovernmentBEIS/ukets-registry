import { createReducer } from '@ngrx/store';
import { mutableOn } from '@registry-web/shared/mutable-on';
import { GenerateLogsActions } from '@generate-logs/actions';
import { Interaction } from '@generate-logs/actions/generate-logs.actions';

export const generateLogsFeatureKey = 'logging';

export interface LoggingState {
  logs: Log[];
  latestLog: Log;
  latestInteraction: Interaction;
  apiRequestCorrelations: { [key: string]: ApiRequestCorrelation };
}

export const initialState: LoggingState = {
  logs: [],
  latestLog: null,
  latestInteraction: null,
  apiRequestCorrelations: {},
};

export const reducer = createReducer(
  initialState,
  mutableOn(GenerateLogsActions.postedLogs, (state, { logs: logsToSend }) => {
    //remove logs to be sent from main array so as to not trigger the effect filter
    state.logs = state.logs.filter(
      ({ id }) => !logsToSend.some(({ id: logId }) => id === logId)
    );
    // remove the correlations that been already applied,
    // and leave those only currently in the state.log array
    state.apiRequestCorrelations = (state.logs || [])
      .map(({ id }) => id)
      .reduce(
        (acc, key) => ({ ...acc, [key]: state.apiRequestCorrelations[key] }),
        {}
      );
  }),
  mutableOn(GenerateLogsActions.generatedLatestLogObject, (state, { log }) => {
    state.latestLog = log;
    state.logs.push(log);
  }),
  mutableOn(
    GenerateLogsActions.receivedUserAction,
    (state, { interaction }) => {
      state.latestInteraction = interaction;
    }
  ),
  mutableOn(
    GenerateLogsActions.apiRequestSuccess,
    GenerateLogsActions.apiRequestFailure,
    (state, { correlation }) => {
      state.apiRequestCorrelations[correlation.logId] = correlation;
    }
  )
);

export interface MappedDiagnosticContext {
  app_name: string;
  app_server: string;
  app_version: number;
  session_identifier: string;
  ets_user_id: string;
  user_id: string;
  cause: string;
  entrypoint: string;
  reception_date: string;
  time_elapsed_ms: number;
}

export interface Exception {
  exception_class: string;
  exception_message: string;
  stacktrace: string;
}

export interface Log {
  mdc: MappedDiagnosticContext;
  action: string;
  id: string;
  exception: Exception;
  line_number: number;
  user_action_identifier: string;
  cause: string;
  class: string;
  version: number;
  source_host: string;
  message: string;
  thread_name: string;
  timestamp: string;
  level: string;
  file: string;
  method: string;
  entrypoint: string;
}

export interface ApiRequestCorrelation {
  interaction: Interaction;
  logId: string;
}
