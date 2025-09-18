import { createAction, props } from '@ngrx/store';

export enum AuthActionTypes {
  IsLoggedInCheck = '[Keycloak] Is Loggedin Check',
  IsLoggedInCheckSuccess = '[Keycloak] Is Logged In Check Success',
  IsLoggedInCheckFail = '[Keycloak] Is Logged In Check Fail',
  Login = '[Keycloak] Login',
  LoginSuccess = '[Keycloak] Login Success',
  LoginFail = '[Keycloak] Login Fail',
  Logout = '[Keycloak] Logout',
  LogoutSuccess = '[Keycloak] Logout Success',
  LogoutFail = '[Keycloak] Logout Fail',
  ExtendSession = '[Keycloak] Extend session',
  ExtendSessionSuccess = '[Keycloak] Extend session success',
  SetSsoSessionIdleTimeout = '[Keycloak] Set sso session idle timeout',
  GetSsoSessionIdleTimeout = '[Keycloak] Get sso session idle timeout',
}

export const IsLoggedInCheck = createAction(AuthActionTypes.IsLoggedInCheck);

export const IsLoggedInCheckSuccess = createAction(
  AuthActionTypes.IsLoggedInCheckSuccess,
  props<{
    authenticated: boolean;
    idmId: string;
    urid: string;
    username: string;
    roles: string[];
    firstName: string;
    lastName: string;
    knownAs: string | unknown;
  }>()
);

export const IsLoggedInCheckFail = createAction(
  AuthActionTypes.IsLoggedInCheckFail,
  props<{
    error: string;
  }>()
);

// Login actions
export const Login = createAction(
  AuthActionTypes.Login,
  props<{ redirectUri: string }>()
);

export const LoginSuccess = createAction(AuthActionTypes.LoginSuccess);

export const LoginFail = createAction(
  AuthActionTypes.LoginFail,
  props<{
    error: string;
  }>()
);

// Logout actions
export const Logout = createAction(
  AuthActionTypes.Logout,
  props<{
    redirectUri?: string;
  }>()
);

export const FetchIsAdmin = createAction('[Auth] Fetch is admin');

export const FetchIsSeniorOrJuniorAdmin = createAction(
  '[Auth] Fetch is Senior or Junior admin'
);

export const FetchIsAdminSuccess = createAction(
  '[Auth] Fetch is admin success',
  props<{ isAdmin: boolean }>()
);

export const FetchIsSeniorOrJuniorAdminSuccess = createAction(
  '[Auth] Fetch is Senior or Junior admin success',
  props<{ isSeniorOrJuniorAdmin: boolean }>()
);

export const FetchIsAdminFail = createAction(
  '[Auth] Fetch is admin Fail',
  props<{
    error: string;
  }>()
);

export const FetchIsSeniorOrJuniorAdminFail = createAction(
  '[Auth] Fetch is Senior or Junior admin Fail',
  props<{
    error: string;
  }>()
);

export const LogoutSuccess = createAction(AuthActionTypes.LogoutSuccess);

export const LogoutFail = createAction(
  AuthActionTypes.LogoutFail,
  props<{
    error: string;
  }>()
);

export const extendSession = createAction(AuthActionTypes.ExtendSession);
export const extendSessionSuccess = createAction(
  AuthActionTypes.ExtendSessionSuccess
);

export const setSsoSessionIdleTimeout = createAction(
  AuthActionTypes.SetSsoSessionIdleTimeout
);

export const getSsoSessionIdleTimeout = createAction(
  AuthActionTypes.GetSsoSessionIdleTimeout,
  props<{ ssoSessionIdleTimeout: number }>()
);
