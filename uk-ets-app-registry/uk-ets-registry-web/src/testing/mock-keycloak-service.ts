import { Injectable } from '@angular/core';
import {
  KeycloakEvent,
  KeycloakEventType,
  KeycloakService,
} from 'keycloak-angular';
import { Subject } from 'rxjs';
import Keycloak from 'keycloak-js';

@Injectable()
export class KeycloakServiceStub implements Partial<KeycloakService> {
  keycloakEvents = new Subject<KeycloakEvent>();

  getKeycloakInstance(): Keycloak.KeycloakInstance {
    return {
      onAuthRefreshSuccess: () =>
        this.keycloakEvents.next({
          type: KeycloakEventType.OnAuthRefreshSuccess,
        }),
      onTokenExpired: () =>
        this.keycloakEvents.next({ type: KeycloakEventType.OnTokenExpired }),
      onAuthError: () =>
        this.keycloakEvents.next({ type: KeycloakEventType.OnAuthError }),
      onAuthLogout: () =>
        this.keycloakEvents.next({ type: KeycloakEventType.OnAuthLogout }),
      onAuthRefreshError: () =>
        this.keycloakEvents.next({
          type: KeycloakEventType.OnAuthRefreshError,
        }),
      onReady: () =>
        this.keycloakEvents.next({ type: KeycloakEventType.OnReady }),
      onAuthSuccess: () =>
        this.keycloakEvents.next({ type: KeycloakEventType.OnAuthSuccess }),
      accountManagement: () => undefined,
      clearToken: () => jest.fn(),
      createAccountUrl: () => '',
      createLoginUrl: () => '',
      createLogoutUrl: () => '',
      createRegisterUrl: () => '',
      hasRealmRole: () => false,
      hasResourceRole: () => false,
      init: () => undefined,
      isTokenExpired: () => false,
      loadUserInfo: () => undefined,
      loadUserProfile: () => undefined,
      login: () => undefined,
      logout: () => undefined,
      register: () => undefined,
      updateToken: () => undefined,
      refreshTokenParsed: { exp: 1606427989 },
      tokenParsed: { exp: 1606427929 },
    };
  }

  updateToken(): Promise<boolean> {
    return Promise.resolve(false);
  }

  get keycloakEvents$(): Subject<KeycloakEvent> {
    return this.keycloakEvents;
  }
}
