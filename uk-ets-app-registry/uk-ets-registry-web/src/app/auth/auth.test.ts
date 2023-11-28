import {
  IsLoggedInCheck,
  IsLoggedInCheckSuccess,
  IsLoggedInCheckFail,
  Logout,
  LogoutSuccess,
  LogoutFail,
} from './auth.actions';
import { reducer, initialState, AuthState } from './auth.reducer';
import { Action } from '@ngrx/store';

describe('Keycloak state', () => {
  let loggedInCheckAction: Action;
  let loggedInCheckSuccessAction: Action;
  let loggedInCheckFailAction: Action;
  let logoutAction: Action;
  let logoutSuccessAction: Action;
  let logoutFailAction: Action;
  let loggedInState: AuthState;

  beforeAll(() => {
    loggedInCheckAction = IsLoggedInCheck();
    loggedInCheckSuccessAction = IsLoggedInCheckSuccess({
      authenticated: true,
      idmId: '10b937af-c938-42f2-a7e9-673f561c8098',
      urid: '',
      username: 'hulk@marvel.com',
      roles: [
        'manage-account',
        'manage-account-links',
        'view-profile',
        'offline_access',
        'uma_authorization',
      ],
      firstName: 'Bruce',
      lastName: 'Banner',
      knownAs: 'KnownAs',
    });
    loggedInCheckFailAction = IsLoggedInCheckFail({
      error: 'Login check fail reason.',
    });

    // Logout actions
    loggedInState = {
      authModel: {
        authenticated: true,
        id: '10b937af-c938-42f2-a7e9-673f561c8098',
        sessionUuid: 'b132b104-179e-45ba-a934-d090b6610312',
        showLoading: false,
        username: 'hulk@marvel.com',
        urid: '',
        roles: [
          'manage-account',
          'manage-account-links',
          'view-profile',
          'offline_access',
          'uma_authorization',
        ],
        firstName: 'Bruce',
        lastName: 'Banner',
        knownAs: 'KnownAs',
      },
      checkingKeycloakStatus: false,
      keycloakError: null,
      isAdmin: false,
      isSeniorOrJuniorAdmin: false,
      ssoSessionIdleTimeout: null,
    };

    logoutAction = Logout({});
    logoutSuccessAction = LogoutSuccess();
    logoutFailAction = LogoutFail({ error: 'Logout error message.' });
  });

  test('sets the flag for a progress indicator while checking keycloak with success', () => {
    const beforeCheckingState = reducer(initialState, {} as any);
    expect(beforeCheckingState.checkingKeycloakStatus).toBe(false);

    const whileCheckingState = reducer(
      beforeCheckingState,
      loggedInCheckAction
    );
    expect(whileCheckingState.checkingKeycloakStatus).toBe(true);

    const afterCheckingState = reducer(
      whileCheckingState,
      loggedInCheckSuccessAction
    );
    expect(afterCheckingState.checkingKeycloakStatus).toBe(false);
  });

  test('sets the flag for a progress indicator while checking keycloak with error', () => {
    const beforeCheckingState = reducer(initialState, {} as any);
    expect(beforeCheckingState.checkingKeycloakStatus).toBe(false);

    const whileCheckingState = reducer(
      beforeCheckingState,
      loggedInCheckAction
    );
    expect(whileCheckingState.checkingKeycloakStatus).toBe(true);

    const afterCheckingState = reducer(
      whileCheckingState,
      loggedInCheckFailAction
    );
    expect(afterCheckingState.checkingKeycloakStatus).toBe(false);
  });

  test('sets the user attributes after successful login check with logged in user', () => {
    const beforeCheckingState = reducer(initialState, {} as any);
    expect(beforeCheckingState.authModel.authenticated).toBe(false);
    expect(beforeCheckingState.authModel.id).toBeNull();
    expect(beforeCheckingState.authModel.username).toBeNull();
    expect(beforeCheckingState.authModel.roles).toBeNull();

    const whileCheckingState = reducer(
      beforeCheckingState,
      loggedInCheckAction
    );
    expect(whileCheckingState.authModel.authenticated).toBe(false);
    expect(whileCheckingState.authModel.id).toBeNull();
    expect(whileCheckingState.authModel.username).toBeNull();
    expect(whileCheckingState.authModel.roles).toBeNull();

    const afterCheckingState = reducer(
      whileCheckingState,
      loggedInCheckSuccessAction
    );
    expect(afterCheckingState.authModel.authenticated).toBe(true);
    expect(afterCheckingState.authModel.id).toEqual(
      '10b937af-c938-42f2-a7e9-673f561c8098'
    );
    expect(afterCheckingState.authModel.username).toEqual('hulk@marvel.com');
    expect(afterCheckingState.authModel.roles).toEqual([
      'manage-account',
      'manage-account-links',
      'view-profile',
      'offline_access',
      'uma_authorization',
    ]);
  });

  test('sets the error message after login check fail', () => {
    const beforeCheckingState = reducer(initialState, {} as any);
    expect(beforeCheckingState.keycloakError).toBeNull();

    const whileCheckingState = reducer(
      beforeCheckingState,
      loggedInCheckAction
    );
    expect(whileCheckingState.keycloakError).toBeNull();

    const afterCheckingState = reducer(
      whileCheckingState,
      loggedInCheckFailAction
    );
    expect(afterCheckingState.keycloakError).toEqual(
      'Login check fail reason.'
    );
  });

  test('clear the keycloak state after logout successfully', () => {
    const beforeLogoutState = reducer(loggedInState, {} as any);
    expect(beforeLogoutState.authModel.authenticated).toBe(true);
    expect(beforeLogoutState.authModel.id).toEqual(
      '10b937af-c938-42f2-a7e9-673f561c8098'
    );
    expect(beforeLogoutState.authModel.username).toEqual('hulk@marvel.com');
    expect(beforeLogoutState.authModel.roles).toEqual([
      'manage-account',
      'manage-account-links',
      'view-profile',
      'offline_access',
      'uma_authorization',
    ]);
    expect(beforeLogoutState.checkingKeycloakStatus).toBe(false);
    expect(beforeLogoutState.keycloakError).toBeNull();

    const whileLoggingOutState = reducer(beforeLogoutState, logoutAction);
    expect(whileLoggingOutState.authModel).toEqual(loggedInState.authModel);
    expect(whileLoggingOutState.checkingKeycloakStatus).toBe(true);
    expect(whileLoggingOutState.keycloakError).toBeNull();

    const afterLogoutState = reducer(whileLoggingOutState, logoutSuccessAction);
    expect(afterLogoutState).toEqual(initialState);
  });

  test('sets the error message after logout failure', () => {
    const beforeLogoutState = reducer(loggedInState, {} as any);
    expect(beforeLogoutState.keycloakError).toBeNull();

    const whileLoggingOutState = reducer(beforeLogoutState, logoutAction);
    expect(whileLoggingOutState.keycloakError).toBeNull();

    const afterLogoutState = reducer(whileLoggingOutState, logoutFailAction);
    expect(afterLogoutState.keycloakError).toEqual('Logout error message.');
  });

  test('sets the flag for a progress indicator while logging out of keycloak with fail', () => {
    const beforeLogoutState = reducer(loggedInState, {} as any);
    expect(beforeLogoutState.checkingKeycloakStatus).toBe(false);

    const whileLoggingOutState = reducer(beforeLogoutState, logoutAction);
    expect(whileLoggingOutState.checkingKeycloakStatus).toBe(true);

    const afterLogoutState = reducer(whileLoggingOutState, logoutFailAction);
    expect(beforeLogoutState.checkingKeycloakStatus).toBe(false);
  });
});
