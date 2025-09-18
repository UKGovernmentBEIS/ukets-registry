import { mutableOn } from '@shared/mutable-on';
import {
  Action,
  createFeatureSelector,
  createReducer,
  createSelector,
} from '@ngrx/store';
import * as RecoveryActions from '@registry-web/dashboard/recovery-methods/actions/recovery-methods.actions';

export const recoveryMethodsFeatureKey = 'recovery-methods';

export interface RecoveryMethodsState {
  recoveryPhoneSet: boolean;
  recoveryEmailSet: boolean;
  hideRecoveryNotificationPage: boolean;
}

export const initialState: RecoveryMethodsState = {
  recoveryPhoneSet: undefined,
  recoveryEmailSet: undefined,
  hideRecoveryNotificationPage: undefined,
};

const recoveryMethodsReducer = createReducer(
  initialState,
  mutableOn(
    RecoveryActions.retrieveUserRecoveryInfoSetSuccess,
    (state, { phoneSet, emailSet, hideRecoveryMethodsNotification }) => {
      state.recoveryPhoneSet = phoneSet;
      state.recoveryEmailSet = emailSet;
      state.hideRecoveryNotificationPage = hideRecoveryMethodsNotification;
    }
  ),
  mutableOn(
    RecoveryActions.hideRecoveryNotificationPageSuccess,
    (state, { hideRecoveryNotificationPage }) => {
      state.hideRecoveryNotificationPage = hideRecoveryNotificationPage;
    }
  )
);

export function reducer(
  state: RecoveryMethodsState | undefined,
  action: Action
) {
  return recoveryMethodsReducer(state, action);
}

const selectRecoveryMethods = createFeatureSelector<RecoveryMethodsState>(
  recoveryMethodsFeatureKey
);

export const selectShowRecoveryNotificationPage = createSelector(
  selectRecoveryMethods,
  (state) => {
    if (
      state.recoveryPhoneSet === undefined &&
      state.recoveryEmailSet === undefined
    ) {
      return undefined;
    } else {
      return {
        phoneSet: !!state.recoveryPhoneSet,
        emailSet: !!state.recoveryEmailSet,
        hideNotificationPage: state.hideRecoveryNotificationPage,
      };
    }
  }
);
