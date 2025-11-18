import { Observable } from 'rxjs';
import { MemoizedSelector } from '@ngrx/store';
import { cold, hot } from 'jasmine-marbles';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { ApiErrorHandlingService } from '@shared/services';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { AccountHolderChangeService } from '@change-account-holder-wizard/service';
import { AccountHolderService } from '@account-opening/account-holder/account-holder.service';
import { AccountHolderContactService } from '@account-opening/account-holder-contact/account-holder-contact.service';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ChangeAccountHolderWizardPaths } from '@change-account-holder-wizard/model';
import { ChangeAccountHolderWizardActions } from '@change-account-holder-wizard/store/actions';
import { ChangeAccountHolderWizardEffects } from '@change-account-holder-wizard/store/effects';
import { ChangeAccountHolderWizardState } from '@change-account-holder-wizard/store/reducers';
import { expect } from '@jest/globals';

describe('ChangeAccountHolderWizardEffects', () => {
  let actions: Observable<any>;
  let effects: ChangeAccountHolderWizardEffects;
  const TEST_ACCOUNT_ID = '123456';
  const MOCK_CURRENT_URL = 'the current url';
  // const TEST_AHC: AccountHolderContactChanged = {};
  // const TEST_AH: AccountHolderInfoChanged = {};
  let apiErrorHandlingService: ApiErrorHandlingService;
  let accountHolderChangeService: AccountHolderChangeService;
  let accountHolderContactService: AccountHolderContactService;
  let accountHolderService: AccountHolderService;
  let mockStore: MockStore<ChangeAccountHolderWizardState>;
  let mockAllocationSelector: MemoizedSelector<
    ChangeAccountHolderWizardState,
    string
  >;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        ChangeAccountHolderWizardEffects,
        provideMockActions(() => actions),
        provideMockStore(),
        { provide: APP_BASE_HREF, useValue: '/' },
        provideHttpClient(),
        provideHttpClientTesting(),
        ApiErrorHandlingService,
        AccountHolderService,
        AccountHolderContactService,
        AccountHolderChangeService,
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl',
        },
      ],
    });

    effects = TestBed.inject(ChangeAccountHolderWizardEffects);
    apiErrorHandlingService = TestBed.inject(ApiErrorHandlingService);
    accountHolderChangeService = TestBed.inject(AccountHolderChangeService);
    accountHolderContactService = TestBed.inject(AccountHolderContactService);
    accountHolderService = TestBed.inject(AccountHolderService);
    mockStore = TestBed.inject(MockStore);
    const httpTesting = TestBed.inject(HttpTestingController);
    mockAllocationSelector = mockStore.overrideSelector(
      selectAccountId,
      TEST_ACCOUNT_ID
    );
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });

  describe('cancelClicked', () => {
    it('should return the [navigateTo] actions with the proper props', () => {
      actions = hot('-a', {
        a: ChangeAccountHolderWizardActions.CANCEL({
          route: MOCK_CURRENT_URL,
        }),
      });

      const expectedEffectResponse = cold('-b', {
        b: ChangeAccountHolderWizardActions.NAVIGATE_TO({
          step: ChangeAccountHolderWizardPaths.CANCEL_REQUEST,
          extras: {
            queryParams: { goBackRoute: MOCK_CURRENT_URL },
          },
        }),
      });

      expect(effects.cancelClicked$).toBeObservable(expectedEffectResponse);
    });
  });

  describe('navigateToRequestSubmitted', () => {
    it('should return the [navigateTo] actions with the proper props', () => {
      actions = hot('-a', {
        a: ChangeAccountHolderWizardActions.SUBMIT_CHANGE_ACCOUNT_HOLDER_REQUEST_SUCCESS(),
      });
      const expectedEffectResponse = cold('-b', {
        b: ChangeAccountHolderWizardActions.NAVIGATE_TO({
          step: ChangeAccountHolderWizardPaths.REQUEST_SUBMITTED,
        }),
      });
      expect(effects.navigateToRequestSubmitted$).toBeObservable(
        expectedEffectResponse
      );
    });
  });

  //   describe('navigateFromAccountHolderContactWorkPage', () => {
  //     it('should return the [navigateTo] actions with the proper props', () => {
  //       const route = [
  //         '/account',
  //         TEST_ACCOUNT_ID,
  //         AccountHolderDetailsWizardPathsModel.BASE_PATH,
  //         AccountHolderDetailsWizardPathsModel.CHECK_UPDATE_REQUEST,
  //       ].join('/');
  //       actions = hot('-a', {
  //         a: setAccountHolderContactWorkDetails({
  //           accountHolderContactChanged: TEST_AHC,
  //         }),
  //       });
  //       const expectedEffectResponse = cold('-b', {
  //         b: AccountHolderDetailsWizardActions.navigateTo({
  //           route: route,
  //           extras: {
  //             skipLocationChange: true,
  //           },
  //         }),
  //       });
  //       expect(effects.navigateFromAccountHolderContactWorkPage$).toBeObservable(
  //         expectedEffectResponse
  //       );
  //     });
  //   });

  //   describe('cancelAccountHolderUpdateRequest', () => {
  //     it('should return the [clearAccountHolderDetailsUpdateRequest, navigateTo] actions with the proper props', () => {
  //       actions = hot('-a', {
  //         a: cancelAccountHolderDetailsUpdateRequest(),
  //       });
  //       const expectedEffectResponse = cold('-(bc)', {
  //         b: clearAccountHolderDetailsUpdateRequest(),
  //         c: AccountHolderDetailsWizardActions.navigateTo({
  //           route: `/account/${TEST_ACCOUNT_ID}`,
  //         }),
  //       });
  //       expect(effects.cancelAccountHolderUpdateRequest$).toBeObservable(
  //         expectedEffectResponse
  //       );
  //     });
  //   });

  //   describe('navigateFromAccountHolderDetailsPage', () => {
  //     it('should return the [navigateTo] actions with the proper props', () => {
  //       const route = [
  //         '/account',
  //         TEST_ACCOUNT_ID,
  //         AccountHolderDetailsWizardPathsModel.BASE_PATH,
  //         AccountHolderDetailsWizardPathsModel.UPDATE_AH_ADDRESS,
  //       ].join('/');
  //       actions = hot('-a', {
  //         a: setAccountHolderDetails({
  //           accountHolderInfoChanged: TEST_AH,
  //         }),
  //       });
  //       const expectedEffectResponse = cold('-b', {
  //         b: AccountHolderDetailsWizardActions.navigateTo({
  //           route: route,
  //           extras: {
  //             skipLocationChange: true,
  //           },
  //         }),
  //       });
  //       expect(effects.navigateFromAccountHolderDetailsPage$).toBeObservable(
  //         expectedEffectResponse
  //       );
  //     });
  //   });
});
