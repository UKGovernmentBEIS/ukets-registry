import { LogsFactoryService } from '@generate-logs/services/logs.factory.service';
import { ActionReducer, MetaReducer } from '@ngrx/store';
import { triggerTimeoutTimer } from '@registry-web/timeout/store/timeout.actions';
import { GenerateLogsActionTypes } from '@generate-logs/actions/generate-logs.actions';
import { AuthModel } from '@registry-web/auth/auth.model';

//All generated UI actions will NOT be dispatched EXCEPT RECEIVED_USER_ACTION
const uiLogsFilterActionTypes: string[] = [
  ...Object.values(GenerateLogsActionTypes).filter(
    (t) => ![GenerateLogsActionTypes.RECEIVED_USER_ACTION].includes(t)
  ),
  triggerTimeoutTimer.type,
];

export const logsFactory =
  (logService: LogsFactoryService): MetaReducer =>
  (reducer: ActionReducer<any>): ActionReducer<any> =>
  (prevState, action) => {
    const newState = reducer(prevState, action);

    if (['store-devtools', '@ngrx/'].some((s) => action.type.startsWith(s))) {
      return newState;
    }

    if (action.type === GenerateLogsActionTypes.POSTED_LOGS_FAILURE) {
      console.error('Failed to post logs: ', (<any>action).logs);
      return newState;
    }

    const previousInteraction = prevState?.logging?.latestInteraction;
    const latestUserAction =
      action.type === GenerateLogsActionTypes.RECEIVED_USER_ACTION
        ? action['interaction']
        : previousInteraction;

    const isUserActionsEqual = latestUserAction === previousInteraction;
    const isActionsBlocklisted = uiLogsFilterActionTypes.includes(action.type);

    if (isUserActionsEqual && !isActionsBlocklisted && latestUserAction) {
      const { sessionUuid, urid, id }: AuthModel = prevState.auth.authModel;
      const log = logService.createLogObj(
        latestUserAction,
        urid,
        sessionUuid,
        id
      );
      log.action =
        GenerateLogsActionTypes.API_REQUEST_FAILURE === action.type
          ? null
          : action.type;
      log.exception =
        GenerateLogsActionTypes.API_REQUEST_FAILURE === action.type
          ? action['exception']
          : null;
      logService.updateActionLog(log);
    }
    return newState;
  };
