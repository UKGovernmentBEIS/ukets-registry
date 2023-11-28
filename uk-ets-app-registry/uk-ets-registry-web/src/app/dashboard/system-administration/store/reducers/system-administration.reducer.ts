import { SystemAdministrationActions } from '../actions';
import { mutableOn } from '@shared/mutable-on';
import { createReducer, Action } from '@ngrx/store';
import { ResetDatabaseResult } from '../../model';

export const systemAdministrationFeatureKey = 'system-administration';

export interface SystemAdministrationState {
  resetDatabaseResult: ResetDatabaseResult;
}

export const initialState: SystemAdministrationState = {
  resetDatabaseResult: null
};

const systemAdministrationReducer = createReducer(
  initialState,
  mutableOn(
    SystemAdministrationActions.submitResetDatabaseSuccess,
    (state, { result }) => {
      state.resetDatabaseResult = result;
    }
  ),
  mutableOn(SystemAdministrationActions.clearDatabaseResetResult, state => {
    resetState(state);
  })
);

export function reducer(
  state: SystemAdministrationState | undefined,
  action: Action
) {
  return systemAdministrationReducer(state, action);
}

function resetState(state) {
  state.resetDatabaseResult = initialState.resetDatabaseResult;
}
