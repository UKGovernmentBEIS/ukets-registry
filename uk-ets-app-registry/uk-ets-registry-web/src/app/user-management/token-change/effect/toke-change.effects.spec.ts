import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable } from 'rxjs';

import { TokeChangeEffects } from './toke-change.effects';
import { AuthApiService } from '../../../auth/auth-api.service';
import { cold, hot } from 'jasmine-marbles';
import { canGoBack, navigateTo } from '@shared/shared.action';
import {
  actionNavigateToEnterCode,
  actionNavigateToEnterReason,
  actionNavigateToVerification,
} from '@user-management/token-change/action/token-change.actions';
import { TokenChangeRoutingPaths } from '@user-management/token-change/model/token-change-root-paths.enum';
import { Action } from '@ngrx/store';
import { MockAuthApiService } from '../../../../testing/mock-auth-api-service';
import { TokenChangeService } from '@user-management/token-change/service/token-change.service';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { EmailChangeState } from '@email-change/reducer';
import { ApiErrorHandlingService } from '@shared/services';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

describe('TokeChangeEffects', () => {
  let actions: Observable<any>;
  let effects: TokeChangeEffects;
  let authService: AuthApiService;
  let tokenChangeService: TokenChangeService;
  let mockStore: MockStore<EmailChangeState>;
  let apiErrorHandlingService: ApiErrorHandlingService;
  let router: Router;

  function testNavigation(
    action: Action,
    expectedGoBackUrl: string,
    expectedDestinationUrl: string,
    effect
  ) {
    actions = hot('-a', { a: action });
    const expectedEffectResponse = cold('-(bc)', {
      b: canGoBack({ goBackRoute: expectedGoBackUrl }),
      c: navigateTo({
        route: expectedDestinationUrl,
      }),
    });
    expect(effect).toBeObservable(expectedEffectResponse);
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([])],
      providers: [
        TokeChangeEffects,
        ApiErrorHandlingService,
        provideMockStore(),
        provideMockActions(() => actions),
        { provide: AuthApiService, useValue: MockAuthApiService },
        {
          provide: TokenChangeService,
          useValue: {
            requestTokenChange: jest.fn(),
          },
        },
      ],
    });

    mockStore = TestBed.inject(MockStore);
    router = TestBed.inject(Router);
    authService = TestBed.inject(AuthApiService);
    tokenChangeService = TestBed.inject(TokenChangeService);
    apiErrorHandlingService = TestBed.inject(ApiErrorHandlingService);
    effects = TestBed.inject(TokeChangeEffects);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });

  describe('Test navigation to Page 1 - Enter Reason', () => {
    it(`should navigate to /${TokenChangeRoutingPaths.BASE_PATH}`, () => {
      actions = hot('-a', {
        a: actionNavigateToEnterReason(),
      });
      const expectedEffectResponse = cold('-(bc)', {
        b: canGoBack({ goBackRoute: `/user-details/my-profile` }),
        c: navigateTo({
          route: `/${TokenChangeRoutingPaths.BASE_PATH}`,
        }),
      });
      expect(effects.actionNavigateToEnterReason$).toBeObservable(
        expectedEffectResponse
      );
    });
  });

  describe('Tests navigation to Page 2 - Enter OTP Code', () => {
    it(`should navigate to /${TokenChangeRoutingPaths.BASE_PATH}/${TokenChangeRoutingPaths.PAGE_2_ENTER_CODE}
    with go back url the /${TokenChangeRoutingPaths.BASE_PATH}`, () => {
      testNavigation(
        actionNavigateToEnterCode(),
        `/${TokenChangeRoutingPaths.BASE_PATH}`,
        `/${TokenChangeRoutingPaths.BASE_PATH}/${TokenChangeRoutingPaths.PAGE_2_ENTER_CODE}`,
        effects.actionNavigateToEnterCode$
      );
    });
  });
});
