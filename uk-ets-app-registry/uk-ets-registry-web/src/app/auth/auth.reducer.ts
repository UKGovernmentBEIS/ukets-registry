import { AuthModel } from './auth.model';
import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import * as KeycloakActions from './auth.actions';
import { getSsoSessionIdleTimeout } from './auth.actions';
import { v4 as uuidv4 } from 'uuid';

export interface KeycloakLoginCheckResponse {
  authenticated: boolean;
  idmId: string;
  urid: string;
  username: string;
  roles: string[];
  firstName: string;
  lastName: string;
  knownAs: string | unknown;
}

export interface AuthState {
  authModel: AuthModel;
  checkingKeycloakStatus: boolean;
  keycloakError?: string;
  isAdmin?: boolean;
  isSeniorOrJuniorAdmin?: boolean;
  ssoSessionIdleTimeout: number;
}

export const initialState: AuthState = {
  authModel: {
    authenticated: false,
    id: null,
    urid: null,
    sessionUuid: null,
    showLoading: false,
    username: null,
    roles: null,
    firstName: null,
    lastName: null,
    knownAs: null,
  },
  checkingKeycloakStatus: false,
  keycloakError: null,
  isAdmin: false,
  isSeniorOrJuniorAdmin: false,
  ssoSessionIdleTimeout: null,
};

const keycloakReducer = createReducer(
  initialState,
  mutableOn(KeycloakActions.IsLoggedInCheck, (state) => {
    state.checkingKeycloakStatus = true;
  }),
  mutableOn(KeycloakActions.IsLoggedInCheckSuccess, (state, response) => {
    state.authModel.authenticated = response.authenticated;
    state.authModel.id = response.idmId;
    state.authModel.urid = response.urid;
    state.authModel.sessionUuid = uuidv4();
    state.authModel.username = response.username;
    state.authModel.roles = response.roles;
    state.authModel.firstName = response.firstName;
    state.authModel.lastName = response.lastName;
    state.authModel.knownAs = response.knownAs;
    state.checkingKeycloakStatus = false;
  }),
  mutableOn(KeycloakActions.IsLoggedInCheckFail, (state, response) => {
    state.keycloakError = response.error;
    state.checkingKeycloakStatus = false;
  }),
  mutableOn(KeycloakActions.Login, (state) => {
    state.checkingKeycloakStatus = true;
  }),
  mutableOn(KeycloakActions.LoginSuccess, (state) => {
    state.checkingKeycloakStatus = false;
  }),
  mutableOn(KeycloakActions.LoginFail, (state, response) => {
    state.keycloakError = response.error;
    state.checkingKeycloakStatus = false;
  }),
  mutableOn(KeycloakActions.Logout, (state) => {
    state.checkingKeycloakStatus = true;
  }),
  mutableOn(KeycloakActions.LogoutSuccess, (state) => {
    state.authModel.authenticated = false;
    state.authModel.id = null;
    state.authModel.urid = null;
    state.authModel.sessionUuid = null;
    state.authModel.username = null;
    state.authModel.roles = null;
    state.authModel.firstName = null;
    state.authModel.lastName = null;
    state.authModel.knownAs = null;
    state.checkingKeycloakStatus = false;
    state.keycloakError = null;
  }),
  mutableOn(KeycloakActions.LogoutFail, (state, response) => {
    state.keycloakError = response.error;
    state.checkingKeycloakStatus = false;
  }),
  mutableOn(KeycloakActions.FetchIsAdminSuccess, (state, response) => {
    state.isAdmin = response.isAdmin;
  }),
  mutableOn(
    KeycloakActions.FetchIsSeniorOrJuniorAdminSuccess,
    (state, response) => {
      state.isSeniorOrJuniorAdmin = response.isSeniorOrJuniorAdmin;
    }
  ),
  mutableOn(getSsoSessionIdleTimeout, (state, { ssoSessionIdleTimeout }) => {
    state.ssoSessionIdleTimeout = ssoSessionIdleTimeout;
  })
);

export function reducer(state: AuthState | undefined, action: Action) {
  return keycloakReducer(state, action);
}
