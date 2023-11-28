import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  hideTimeoutDialog,
  showTimeoutDialog,
} from '@registry-web/timeout/store/timeout.actions';
import { SharedState } from '@shared/shared.reducer';

export const timeoutFeatureKey = 'timeout';

export interface TimeoutState {
  isTimeoutDialogVisible: boolean;
}

export const initialState: TimeoutState = {
  isTimeoutDialogVisible: false,
};

const timeoutReducer = createReducer(
  initialState,
  mutableOn(showTimeoutDialog, (state) => {
    state.isTimeoutDialogVisible = true;
  }),
  mutableOn(hideTimeoutDialog, (state) => {
    state.isTimeoutDialogVisible = false;
  })
);

export function reducer(state: SharedState | undefined, action: Action) {
  return timeoutReducer(state, action);
}
