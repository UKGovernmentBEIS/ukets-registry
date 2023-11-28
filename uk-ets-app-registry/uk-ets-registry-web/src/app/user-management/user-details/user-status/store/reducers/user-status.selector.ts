import { createFeatureSelector, createSelector } from '@ngrx/store';
import { userStatusFeatureKey, UserStatusState } from './user-status.reducer';

const selectUserStatusState =
  createFeatureSelector<UserStatusState>(userStatusFeatureKey);

export const selectAllowedUserStatusActions = createSelector(
  selectUserStatusState,
  (state) => state.allowedUserStatusActions
);

export const selectUserStatusAction = createSelector(
  selectUserStatusState,
  (state) => state.userStatusAction
);

export const selectUserSuspendedByTheSystem = createSelector(
  selectUserStatusState,
  (state) => state.userSuspendedByTheSystem
);
