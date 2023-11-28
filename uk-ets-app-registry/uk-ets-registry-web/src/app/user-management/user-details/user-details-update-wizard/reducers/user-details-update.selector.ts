import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  userDetailsUpdateFeatureKey,
  UserDetailsUpdateState,
} from '@user-update/reducers/user-details-update.reducer';
import { selectLoggedInUser } from '@registry-web/auth/auth.selector';
import { UserUpdateDetailsType } from '@user-update/model';

const selectUserDetailsUpdateState =
  createFeatureSelector<UserDetailsUpdateState>(userDetailsUpdateFeatureKey);

export const selectIsLoadedFromMyProfilePage = createSelector(
  selectUserDetailsUpdateState,
  (state) => state.isMyProfilePage
);

export const selectCurrentUserDetailsInfo = createSelector(
  selectUserDetailsUpdateState,
  (state) => state.userDetails
);

export const selectUserDetailsUpdateType = createSelector(
  selectUserDetailsUpdateState,
  (state) => state.updateType
);

export const selectNewUserDetailsInfo = createSelector(
  selectUserDetailsUpdateState,
  (state) => state.newUserDetails
);

export const selectUserDetailsUpdateInfo = createSelector(
  selectCurrentUserDetailsInfo,
  selectNewUserDetailsInfo,
  (currentUserDetails, newUserDetails) => {
    if (newUserDetails) {
      return { ...newUserDetails };
    } else {
      return { ...currentUserDetails };
    }
  }
);

export const selectDeactivationComment = createSelector(
  selectUserDetailsUpdateState,
  (state) => state.deactivationComment
);

export const selectSubmittedRequestIdentifier = createSelector(
  selectUserDetailsUpdateState,
  (state) => state.submittedRequestIdentifier
);

export const selectIsLoggedInUserSameAsDeactivated = createSelector(
  selectUserDetailsUpdateState,
  selectLoggedInUser,
  (selected, loggedIn) =>
    selected?.userDetails?.urid === loggedIn?.urid &&
    selected?.updateType === UserUpdateDetailsType.DEACTIVATE_USER
);
