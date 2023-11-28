import { Observable } from 'rxjs';
import { AccountHolderDetailsWizardNavigationEffects } from './account-holder-details-wizard-navigation.effects';
import { MemoizedSelector } from '@ngrx/store';
import { cold, hot } from 'jasmine-marbles';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { ApiErrorHandlingService } from '@shared/services';
import { AccountHolderUpdateService } from '@account-management/account/account-holder-details-wizard/services';
import { RouterModule } from '@angular/router';
import { APP_BASE_HREF } from '@angular/common';
import { UK_ETS_REGISTRY_API_BASE_URL } from '../../../../app.tokens';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import {
  cancelAccountHolderDetailsUpdateRequest,
  cancelClicked,
  clearAccountHolderDetailsUpdateRequest,
  setAccountHolderContactWorkDetails,
  setAccountHolderDetails,
  submitUpdateRequestSuccess,
} from '../actions/account-holder-details-wizard.action';
import { AccountHolderDetailsWizardActions } from '@account-management/account/account-holder-details-wizard/actions';
import {
  AccountHolderContactChanged,
  AccountHolderDetailsWizardPathsModel,
  AccountHolderInfoChanged,
} from '@account-management/account/account-holder-details-wizard/model';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { AccountState } from '@account-management/account/account-details/account.reducer';

describe('AccountHolderDetailsWizardNavigationEffects', () => {
  let actions: Observable<any>;
  let effects: AccountHolderDetailsWizardNavigationEffects;
  const TEST_ACCOUNT_ID = '123456';
  const MOCK_CURRENT_URL = 'the current url';
  const TEST_AHC: AccountHolderContactChanged = {};
  const TEST_AH: AccountHolderInfoChanged = {};
  let apiErrorHandlingService: ApiErrorHandlingService;
  let accountHolderUpdateService: AccountHolderUpdateService;
  let mockStore: MockStore<AccountState>;
  let mockAllocationSelector: MemoizedSelector<AccountState, string>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([]), HttpClientTestingModule],
      providers: [
        AccountHolderDetailsWizardNavigationEffects,
        provideMockActions(() => actions),
        provideMockStore(),
        { provide: APP_BASE_HREF, useValue: '/' },
        ApiErrorHandlingService,
        AccountHolderUpdateService,
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl',
        },
      ],
    });

    effects = TestBed.inject(AccountHolderDetailsWizardNavigationEffects);
    apiErrorHandlingService = TestBed.inject(ApiErrorHandlingService);
    accountHolderUpdateService = TestBed.inject(AccountHolderUpdateService);
    mockStore = TestBed.inject(MockStore);
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
      const route = [
        '/account',
        TEST_ACCOUNT_ID,
        AccountHolderDetailsWizardPathsModel.BASE_PATH,
        AccountHolderDetailsWizardPathsModel.CANCEL_UPDATE_REQUEST,
      ].join('/');
      actions = hot('-a', { a: cancelClicked({ route: MOCK_CURRENT_URL }) });
      const expectedEffectResponse = cold('-b', {
        b: AccountHolderDetailsWizardActions.navigateTo({
          route: route,
          extras: {
            queryParams: { goBackRoute: MOCK_CURRENT_URL },
            skipLocationChange: true,
          },
        }),
      });
      expect(effects.cancelClicked$).toBeObservable(expectedEffectResponse);
    });
  });

  describe('navigateToRequestSubmitted', () => {
    it('should return the [navigateTo] actions with the proper props', () => {
      const route = [
        '/account',
        TEST_ACCOUNT_ID,
        AccountHolderDetailsWizardPathsModel.BASE_PATH,
        AccountHolderDetailsWizardPathsModel.REQUEST_SUBMITTED,
      ].join('/');
      actions = hot('-a', { a: submitUpdateRequestSuccess({ requestId: '' }) });
      const expectedEffectResponse = cold('-b', {
        b: AccountHolderDetailsWizardActions.navigateTo({
          route: route,
          extras: {
            skipLocationChange: true,
          },
        }),
      });
      expect(effects.navigateToRequestSubmitted$).toBeObservable(
        expectedEffectResponse
      );
    });
  });

  describe('navigateFromAccountHolderContactWorkPage', () => {
    it('should return the [navigateTo] actions with the proper props', () => {
      const route = [
        '/account',
        TEST_ACCOUNT_ID,
        AccountHolderDetailsWizardPathsModel.BASE_PATH,
        AccountHolderDetailsWizardPathsModel.CHECK_UPDATE_REQUEST,
      ].join('/');
      actions = hot('-a', {
        a: setAccountHolderContactWorkDetails({
          accountHolderContactChanged: TEST_AHC,
        }),
      });
      const expectedEffectResponse = cold('-b', {
        b: AccountHolderDetailsWizardActions.navigateTo({
          route: route,
          extras: {
            skipLocationChange: true,
          },
        }),
      });
      expect(effects.navigateFromAccountHolderContactWorkPage$).toBeObservable(
        expectedEffectResponse
      );
    });
  });

  describe('cancelAccountHolderUpdateRequest', () => {
    it('should return the [clearAccountHolderDetailsUpdateRequest, navigateTo] actions with the proper props', () => {
      actions = hot('-a', {
        a: cancelAccountHolderDetailsUpdateRequest(),
      });
      const expectedEffectResponse = cold('-(bc)', {
        b: clearAccountHolderDetailsUpdateRequest(),
        c: AccountHolderDetailsWizardActions.navigateTo({
          route: `/account/${TEST_ACCOUNT_ID}`,
        }),
      });
      expect(effects.cancelAccountHolderUpdateRequest$).toBeObservable(
        expectedEffectResponse
      );
    });
  });

  describe('navigateFromAccountHolderDetailsPage', () => {
    it('should return the [navigateTo] actions with the proper props', () => {
      const route = [
        '/account',
        TEST_ACCOUNT_ID,
        AccountHolderDetailsWizardPathsModel.BASE_PATH,
        AccountHolderDetailsWizardPathsModel.UPDATE_AH_ADDRESS,
      ].join('/');
      actions = hot('-a', {
        a: setAccountHolderDetails({
          accountHolderInfoChanged: TEST_AH,
        }),
      });
      const expectedEffectResponse = cold('-b', {
        b: AccountHolderDetailsWizardActions.navigateTo({
          route: route,
          extras: {
            skipLocationChange: true,
          },
        }),
      });
      expect(effects.navigateFromAccountHolderDetailsPage$).toBeObservable(
        expectedEffectResponse
      );
    });
  });
});
