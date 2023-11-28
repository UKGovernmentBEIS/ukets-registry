import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  userDetailsFeatureKey,
  UserDetailsState,
} from './user-details.reducer';
import { UserStatus } from '@shared/user';
import { selectLoggedInUser } from '@registry-web/auth/auth.selector';

export * from './user-details.reducer';

export const selectUserDetailsState = createFeatureSelector<UserDetailsState>(
  userDetailsFeatureKey
);

export const areUserDetailsLoaded = createSelector(
  selectUserDetailsState,
  (state) => state.userDetailsLoaded
);

export const selectUserDetails = createSelector(
  selectUserDetailsState,
  (state) => state.userDetails
);

export const selectUrid = createSelector(selectUserDetailsState, (state) => {
  if (
    !state.userDetails ||
    !state.userDetails.attributes ||
    !state.userDetails.attributes.urid
  ) {
    return null;
  }
  return state.userDetails.attributes.urid[0];
});

export const selectUserStatus = createSelector(
  selectUserDetailsState,
  (state) => {
    if (!state.userDetails) {
      return null;
    }
    return state.userDetails.attributes.state[0] as UserStatus;
  }
);

export const selectARsInAccountDetails = createSelector(
  selectUserDetailsState,
  (state) => state.ARs
);

export const selectUserHistoryAndComments = createSelector(
  selectUserDetailsState,
  (state) => state.userHistory
);

export const selectUserFiles = createSelector(
  selectUserDetailsState,
  (state) => state.userFiles
);

export const selectEnrolmentKeyDetails = createSelector(
  selectUserDetailsState,
  (state) => state.enrolmentKeyDetails
);

export const selectUserDetailsEmail = createSelector(
  selectUserDetails,
  (details) => details?.email
);

export const selectUserDetailsPendingTasks = createSelector(
  selectUserDetailsState,
  (state) => state.hasUserDetailsUpdatePendingApproval
);
