import { createAction, props } from '@ngrx/store';
import { ResetDatabaseResult } from '../../model';

export enum SystemAdministrationActionTypes {
  SUBMIT_RESET_DB_ACTION = '[System Administration] Submit Reset DB',
  SUBMIT_RESET_DB_ACTION_SUCCESS = '[System Administration] Submit Reset DB Success',
  CLEAR_DB_RESET_RESULT = '[System Administration] Clear DB Reset Result'
}

export const submitResetDatabaseAction = createAction(
  SystemAdministrationActionTypes.SUBMIT_RESET_DB_ACTION
);

export const submitResetDatabaseSuccess = createAction(
  SystemAdministrationActionTypes.SUBMIT_RESET_DB_ACTION_SUCCESS,
  props<{ result: ResetDatabaseResult }>()
);

export const clearDatabaseResetResult = createAction(
  SystemAdministrationActionTypes.CLEAR_DB_RESET_RESULT
);
