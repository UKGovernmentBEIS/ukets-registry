import { Observable } from 'rxjs';
import { EmailChangeNavigationEffect } from '@email-change/effect/email-change-navigation.effects';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { cold, hot } from 'jasmine-marbles';
import { canGoBack, navigateTo } from '@shared/shared.action';
import { EmailChangeRoutePath } from '@email-change/model';
import {
  navigateToEmailChangeWizard,
  navigateToVerificationPage
} from '@email-change/action/email-change.actions';
import { Action } from '@ngrx/store';
describe('emailChangeNavigationEffect', () => {
  let actions: Observable<any>;
  let effects: EmailChangeNavigationEffect;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        EmailChangeNavigationEffect,
        provideMockStore(),
        provideMockActions(() => actions)
      ]
    });
    effects = TestBed.inject(EmailChangeNavigationEffect);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });

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
        route: expectedDestinationUrl
      })
    });
    expect(effect).toBeObservable(expectedEffectResponse);
  }

  describe('navigateToEmailChangeWizard$', () => {
    it(`should navigate to /${EmailChangeRoutePath.BASE_PATH}`, () => {
      actions = hot('-a', {
        a: navigateToEmailChangeWizard({
          urid: '',
          caller: {
            route: ''
          }
        })
      });
      const expectedEffectResponse = cold('-(b)', {
        b: navigateTo({
          route: `/${EmailChangeRoutePath.BASE_PATH}`
        })
      });
      expect(effects.navigateToEmailChangeWizard$).toBeObservable(
        expectedEffectResponse
      );
    });
  });

  describe('navigateToVerificationPage$', () => {
    it(`should navigate to /${EmailChangeRoutePath.BASE_PATH}/${EmailChangeRoutePath.EMAIL_CHANGE_REQUEST_VERIFICATION_PATH}
      without go back url.`, () => {
      testNavigation(
        navigateToVerificationPage({ newEmail: 'test@test.com' }),
        null,
        `/${EmailChangeRoutePath.BASE_PATH}/${EmailChangeRoutePath.EMAIL_CHANGE_REQUEST_VERIFICATION_PATH}`,
        effects.navigateToVerificationPage$
      );
    });
  });
});
