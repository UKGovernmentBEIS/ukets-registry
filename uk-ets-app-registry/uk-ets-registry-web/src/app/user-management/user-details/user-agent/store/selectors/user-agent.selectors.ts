import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  userAgentFeatureKey,
  UserAgentState,
} from '@user-agent/store/reducers';

const selectUserAgentState =
  createFeatureSelector<UserAgentState>(userAgentFeatureKey);

export const selectAgentType = createSelector(
  selectUserAgentState,
  (userAgentState) => userAgentState.type
);

export const selectPublicAgentDetails = createSelector(
  selectUserAgentState,
  (userAgentState) => userAgentState.details
);

export const selectCallerRoute = createSelector(
  selectUserAgentState,
  (userAgentState) => userAgentState.caller.route
);

export const selectCallerExtras = createSelector(
  selectUserAgentState,
  (userAgentState) => userAgentState.caller.extras
);

export const selectUserURID = createSelector(
  selectUserAgentState,
  (userAgentState) => userAgentState.urid
);

export const selectUserAgentLoaded = createSelector(
  selectUserAgentState,
  (userAgentState) => userAgentState.loaded
);
