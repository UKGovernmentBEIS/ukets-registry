import { Observable } from 'rxjs';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { ApiErrorHandlingService } from '@shared/services';
import { PasswordChangeNavigationEffects } from '@user-management/password-change/effect/password-change-navigation.effects';
import { PasswordChangeState } from '@user-management/password-change/reducer';
import { RequestPasswordChangeService } from '@user-management/password-change/service';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { MockAuthApiService } from '../../../../testing/mock-auth-api-service';
import { cold, hot } from 'jasmine-marbles';
import { navigateToPasswordChangeWizard } from '@user-management/password-change/action/password-change.actions';
import { navigateTo } from '@shared/shared.action';
import { PasswordChangeRoutePaths } from '@user-management/password-change/model';

describe('PasswordChangeNavigationEffects', () => {
  let actions: Observable<any>;
  let effects: PasswordChangeNavigationEffects;
  let mockStore: MockStore<PasswordChangeState>;
  let apiErrorHandlingService: ApiErrorHandlingService;
  let requestPasswordChangeService: RequestPasswordChangeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        PasswordChangeNavigationEffects,
        ApiErrorHandlingService,
        provideMockStore(),
        provideMockActions(() => actions),
        { provide: AuthApiService, useValue: MockAuthApiService },
        {
          provide: RequestPasswordChangeService,
          useValue: {
            changePassword: jest.fn()
          }
        }
      ]
    });
    effects = TestBed.inject(PasswordChangeNavigationEffects);
    mockStore = TestBed.inject(MockStore);
    requestPasswordChangeService = TestBed.inject(RequestPasswordChangeService);
    apiErrorHandlingService = TestBed.inject(ApiErrorHandlingService);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });

  describe('navigateToPasswordChangeWizard', () => {
    it('should return the [Account Holder details request update] Navigate to', () => {
      actions = hot('-a', {
        a: navigateToPasswordChangeWizard({ email: 'test@test.com' })
      });
      const expectedEffectResponse = cold('-b', {
        b: navigateTo({
          route: `/${PasswordChangeRoutePaths.BASE_PATH}`
        })
      });
      expect(effects.navigateToPasswordChangeWizard$).toBeObservable(
        expectedEffectResponse
      );
    });
  });
});
