import { Observable } from 'rxjs';
import { AuthoritySettingEffect } from '@authority-setting/effect/authority-setting.effects';
import { AuthoritySettingService } from '@authority-setting/service';
import { ApiErrorHandlingService } from '@shared/services';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { AuthoritySettingState } from '@authority-setting/reducer';
import { TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { provideMockActions } from '@ngrx/effects/testing';
import { cold, hot } from 'jasmine-marbles';
import {
  cancelAuthoritySetting,
  fetchEnrolledUser,
  fetchEnrolledUserSuccess,
  removeUserFromAuthorityUsers,
  removeUserFromAuthorityUsersSuccess,
  setUserAsAuthority,
  setUserAsAuthoritySuccess,
  startAuthoritySettingWizard
} from '@authority-setting/action';
import { canGoBack, navigateTo } from '@shared/shared.action';
import { AuthoritySettingRoutePathsModel } from '@authority-setting/model/authority-setting-route-paths.model';
import { EnrolledUser } from '@authority-setting/model';
import { Action } from '@ngrx/store';

describe('AuthoritySettingEffect', () => {
  const MOCK_CURRENT_URL = '/mock-current-url';
  let actions: Observable<any>;
  let effects: AuthoritySettingEffect;
  let authoritySettingService: AuthoritySettingService;
  let apiErrorHandlingService: ApiErrorHandlingService;
  let mockStore: MockStore<AuthoritySettingState>;
  const urid = 'UK213123';
  const enrolledUser: EnrolledUser = {
    firstName: 'firstName',
    lastName: 'lastName',
    userId: urid,
    email: 'email',
    knownAs: 'knownAs'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              _routerState: {
                url: MOCK_CURRENT_URL
              }
            }
          }
        },
        AuthoritySettingEffect,
        ApiErrorHandlingService,
        provideMockStore(),
        provideMockActions(() => actions),
        {
          provide: AuthoritySettingService,
          useValue: {
            fetchEnrolledUser: jest.fn(),
            setUserAsAuthority: jest.fn(),
            removeUserFromAuthorityUsers: jest.fn()
          }
        }
      ]
    });

    effects = TestBed.inject(AuthoritySettingEffect);
    authoritySettingService = TestBed.inject(AuthoritySettingService);
    apiErrorHandlingService = TestBed.inject(ApiErrorHandlingService);
    mockStore = TestBed.inject(MockStore);
  });

  function verifyNavigationAndGoBackUrl(command: {
    action: Action;
    goBackRoute: string;
    route: string;
    effect: Observable<any>;
  }) {
    actions = hot('-a', { a: command.action });
    const expectedEffectResponse = cold('-(bc)', {
      b: canGoBack({
        goBackRoute: command.goBackRoute
      }),
      c: navigateTo({
        route: command.route
      })
    });
    expect(command.effect).toBeObservable(expectedEffectResponse);
  }

  function verifyNavigation(command: {
    action: Action;
    route: string;
    effect: Observable<any>;
  }) {
    actions = hot('-a', { a: command.action });
    const expectedEffectResponse = cold('-(b)', {
      b: navigateTo({
        route: command.route
      })
    });
    expect(command.effect).toBeObservable(expectedEffectResponse);
  }

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });

  describe('Start Wizard', () => {
    it('should return the [navigateTo] with the right url', () => {
      verifyNavigation({
        action: startAuthoritySettingWizard(),
        route: `/${AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION}/${AuthoritySettingRoutePathsModel.BASE_PAGE}`,
        effect: effects.startAuthoritySettingWizard$
      });
    });
  });

  describe('Fetch Enrolled user', () => {
    it('should return the fetchEnrolledUserSuccess action which carries the fetched enrolled user', () => {
      const fetchEnrolledUserAction = fetchEnrolledUser({ urid });
      const fetchEnrolledUserSuccessAction = fetchEnrolledUserSuccess({
        enrolledUser
      });
      actions = hot('-a', { a: fetchEnrolledUserAction });
      const serviceResponse = cold('-a|', {
        a: enrolledUser
      });
      const expectedEffectResponse = cold('--b', {
        b: fetchEnrolledUserSuccessAction
      });
      authoritySettingService.fetchEnrolledUser = jest.fn(
        () => serviceResponse
      );
      expect(effects.fetchEnrolledUser$).toBeObservable(expectedEffectResponse);
    });
  });

  describe('Fetch enrolled user success', () => {
    it('should return the [navigateTo] with the right urls', () => {
      const route = [
        AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION,
        AuthoritySettingRoutePathsModel.BASE_PAGE,
        AuthoritySettingRoutePathsModel.CHECK_UPDATE_REQUEST
      ].join('/');
      verifyNavigation({
        action: fetchEnrolledUserSuccess({ enrolledUser }),
        route: `/${route}`,
        effect: effects.fetchEnrolledUserSuccess$
      });
    });
  });

  describe('Set user as authority', () => {
    it('should return the setUserAsAuthoritySuccessAction action', () => {
      const setUserAsAuthorityAction = setUserAsAuthority({ urid });
      const setUserAsAuthoritySuccessAction = setUserAsAuthoritySuccess();
      actions = hot('-a', { a: setUserAsAuthorityAction });
      const serviceResponse = cold('-a|', {
        a: 'any 200 OK response'
      });
      const expectedEffectResponse = cold('--b', {
        b: setUserAsAuthoritySuccessAction
      });
      authoritySettingService.setUserAsAuthority = jest.fn(
        () => serviceResponse
      );

      expect(effects.setUserAsAuthority$).toBeObservable(
        expectedEffectResponse
      );
    });
  });

  describe('Set user as authority success', () => {
    it('should return the [canGoBack, navigateTo] with the right urls', () => {
      const route = [
        AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION,
        AuthoritySettingRoutePathsModel.BASE_PAGE,
        AuthoritySettingRoutePathsModel.UPDATE_REQUEST_SUCCESS
      ].join('/');
      verifyNavigationAndGoBackUrl({
        effect: effects.setUserAsAuthoritySuccess$,
        route: `/${route}`,
        goBackRoute: null,
        action: setUserAsAuthoritySuccess()
      });
    });
  });

  describe('Remove user from authority users', () => {
    it('should return the fetchEnrolledUserSuccess action which carries the fetched enrolled user', () => {
      const removeUserFromAuthorityUsersAction = removeUserFromAuthorityUsers({
        urid
      });
      const removeUserFromAuthorityUsersSuccessAction = removeUserFromAuthorityUsersSuccess();
      actions = hot('-a', { a: removeUserFromAuthorityUsersAction });
      const serviceResponse = cold('-a|', {
        a: 'any 200 OK response'
      });
      const expectedEffectResponse = cold('--b', {
        b: removeUserFromAuthorityUsersSuccessAction
      });
      authoritySettingService.removeUserFromAuthorityUsers = jest.fn(
        () => serviceResponse
      );

      expect(effects.removeUserFromAuthorityUsers$).toBeObservable(
        expectedEffectResponse
      );
    });
  });

  describe('Remove user from authority users success', () => {
    it('should return the [canGoBack, navigateTo] with the right urls', () => {
      const route = [
        AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION,
        AuthoritySettingRoutePathsModel.BASE_PAGE,
        AuthoritySettingRoutePathsModel.UPDATE_REQUEST_SUCCESS
      ].join('/');
      verifyNavigationAndGoBackUrl({
        effect: effects.removeUserFromAuthorityUsersSuccess$,
        route: `/${route}`,
        goBackRoute: null,
        action: removeUserFromAuthorityUsersSuccess()
      });
    });
  });

  describe('Cancel authority setting and go back to registry administration', () => {
    it('should return the [navigateTo] with the right urls', () => {
      verifyNavigation({
        effect: effects.cancelAuthoritySetting$,
        route: `/${AuthoritySettingRoutePathsModel.REGISTRY_ADMINISTRATION}`,
        action: cancelAuthoritySetting()
      });
    });
  });
});
