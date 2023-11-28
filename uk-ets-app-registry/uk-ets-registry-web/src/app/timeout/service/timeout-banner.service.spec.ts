import { TestBed } from '@angular/core/testing';

import { TimeoutBannerService } from './timeout-banner.service';
import { KeycloakService } from 'keycloak-angular';
import { KeycloakServiceStub } from '../../../testing/mock-keycloak-service';
import { MockStore, provideMockStore } from '@ngrx/store/testing';

describe('TimeoutBannerService', () => {
  let service: TimeoutBannerService;
  let store: MockStore;
  let keycloakService: KeycloakService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: KeycloakService, useClass: KeycloakServiceStub },
        provideMockStore(),
      ],
    });
    service = TestBed.inject(TimeoutBannerService);
    store = TestBed.inject(MockStore);
    keycloakService = TestBed.inject(KeycloakService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return expiration period', () => {
    expect(service.getExpirationPeriod()).toEqual(60000);
  });

  it('should trigger timeout timer on token expired', () => {
    const storeSpy = jest.spyOn(store, 'dispatch');
    keycloakService.getKeycloakInstance().onTokenExpired();
    expect(storeSpy).toHaveBeenCalledWith({
      eventType: 6,
      type: '[Shared] Trigger timeout timer',
    });
  });

  it('should trigger timeout timer on auth refresh', () => {
    const storeSpy = jest.spyOn(store, 'dispatch');
    keycloakService.getKeycloakInstance().onAuthRefreshSuccess();
    expect(storeSpy).toHaveBeenCalledWith({
      eventType: 3,
      type: '[Shared] Trigger timeout timer',
    });
  });

  it('should not trigger timeout timer on any other event', () => {
    const storeSpy = jest.spyOn(store, 'dispatch');
    keycloakService
      .getKeycloakInstance()
      .onAuthError({ error: 'error', error_description: 'description' });
    expect(storeSpy).not.toHaveBeenCalled();

    keycloakService.getKeycloakInstance().onAuthLogout();
    expect(storeSpy).not.toHaveBeenCalled();

    keycloakService.getKeycloakInstance().onAuthRefreshError();
    expect(storeSpy).not.toHaveBeenCalled();

    keycloakService.getKeycloakInstance().onAuthSuccess();
    expect(storeSpy).not.toHaveBeenCalled();

    keycloakService.getKeycloakInstance().onReady();
    expect(storeSpy).not.toHaveBeenCalled();
  });
});
