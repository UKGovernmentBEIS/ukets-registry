import { createFeatureSelector, createSelector } from '@ngrx/store';
import { State } from 'src/app/reducers';
import { AuthState } from './auth.reducer';

const selectAuth = createFeatureSelector<State, AuthState>('auth');

export const selectState = createSelector(selectAuth, (authState) => authState);

export const isAuthenticated = createSelector(
  selectAuth,
  (authState) => authState.authModel.authenticated
);

export const selectLoggedInUser = createSelector(
  selectAuth,
  (authState) => authState.authModel
);

export const isSeniorOrJuniorAdmin = createSelector(
  selectAuth,
  (authState) => authState.isSeniorOrJuniorAdmin
);

export const isAdmin = createSelector(
  selectAuth,
  (authState) => authState.isAdmin
);

export const isAuthorizedRepresentative = createSelector(
  selectAuth,
  (authState) => authState.authModel.roles.includes('authorized-representative')
);

export const isAuthorityUser = createSelector(selectAuth, (authState) =>
  authState.authModel.roles.includes('authority-user')
);

export const isReadOnlyAdmin = createSelector(selectAuth, (authState) =>
  authState.authModel.roles.includes('readonly-administrator')
);

export const isSeniorAdmin = createSelector(selectAuth, (authState) =>
  authState.authModel.roles.includes('senior-registry-administrator')
);

export const selectUrid = createSelector(
  selectAuth,
  (authState) => authState.authModel.urid
);
export const selectSessionUuid = createSelector(
  selectAuth,
  (authState) => authState.authModel.sessionUuid
);

export const selectUserId = createSelector(
  selectAuth,
  (authState) => authState.authModel.id
);

export const selectSsoSessionIdleTimeout = createSelector(
  selectAuth,
  (state) => state.ssoSessionIdleTimeout
);
