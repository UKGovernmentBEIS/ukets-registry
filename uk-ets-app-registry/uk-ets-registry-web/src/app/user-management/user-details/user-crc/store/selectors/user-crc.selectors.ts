import { createFeatureSelector, createSelector } from '@ngrx/store';
import { userCrcFeatureKey, UserCrcState } from '@user-crc/store/reducers';

const selectUserCrcState =
  createFeatureSelector<UserCrcState>(userCrcFeatureKey);

export const selectCrcDetails = createSelector(
  selectUserCrcState,
  (userCrcState) => userCrcState.details
);

export const selectCrcSubmitted = createSelector(
  selectUserCrcState,
  (userCrcState) => userCrcState.crcSubmitted
);

export const selectCallerRoute = createSelector(
  selectUserCrcState,
  (userCrcState) => userCrcState.caller.route
);

export const selectCallerExtras = createSelector(
  selectUserCrcState,
  (userCrcState) => userCrcState.caller.extras
);

export const selectUserURID = createSelector(
  selectUserCrcState,
  (userCrcState) => userCrcState.urid
);

export const selectUserCrcLoaded = createSelector(
  selectUserCrcState,
  (userCrcState) => userCrcState.loaded
);
