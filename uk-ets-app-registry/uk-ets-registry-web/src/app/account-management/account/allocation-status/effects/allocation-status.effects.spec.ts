import { Observable } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { ApiErrorHandlingService } from '@shared/services';
import { AllocationStatusEffect } from '@allocation-status/effects/allocation-status.effects';
import { cold, hot } from 'jasmine-marbles';
import {
  cancel,
  continueToUpdateRequestVerification,
  fetchAllocationStatus,
  fetchAllocationStatusSuccess,
  navigateToAccountAllocation,
  navigateToCancel,
  navigateToVerificationPage,
  prepareWizard,
  resetAllocationStatusState,
  updateAllocationStatus,
  updateAllocationStatusSuccess,
} from '@allocation-status/actions/allocation-status.actions';
import { canGoBack, errors, navigateTo } from '@shared/shared.action';
import { MenuItemEnum } from '@registry-web/account-management/account/account-details/model/account-side-menu.model';
import { ActivatedRoute } from '@angular/router';
import {
  AccountAllocationStatus,
  UpdateAllocationStatusRequest,
} from '@allocation-status/model';
import { AllocationStatus } from '@shared/model/account';
import { ErrorSummary } from '@shared/error-summary';
import { HttpErrorResponse } from '@angular/common/http';
import { AllocationStatusRoutePathsModel } from '@allocation-status/model/allocation-status-route-paths.model';
import { Action, MemoizedSelector } from '@ngrx/store';
import { AccountState } from '@account-management/account/account-details/account.reducer';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { AccountAllocationService } from '@account-management/service/account-allocation.service';
import { selectUpdateRequest } from '@allocation-status/reducers/allocation-status.selector';
import { getRouteFromArray } from '@shared/utils/router.utils';

describe('AllocationStatusEffects', () => {
  const TEST_ACCOUNT_ID = '123456';
  const MOCK_CURRENT_URL = 'the current url';
  let actions: Observable<any>;
  let effects: AllocationStatusEffect;
  let allocationStatusService: AccountAllocationService;
  let apiErrorHandlingService: ApiErrorHandlingService;

  let mockStore: MockStore<AccountState>;
  let mockAllocationSelector: MemoizedSelector<AccountState, string>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              params: {
                accountId: TEST_ACCOUNT_ID,
              },
              _routerState: {
                url: MOCK_CURRENT_URL,
              },
            },
          },
        },
        AllocationStatusEffect,
        ApiErrorHandlingService,
        provideMockStore(),
        provideMockActions(() => actions),
        {
          provide: AccountAllocationService,
          useValue: {
            fetchAllocation: jest.fn(),
            fetchAllocationStatus: jest.fn(),
            updateAllocationStatus: jest.fn(),
          },
        },
      ],
    });

    effects = TestBed.inject(AllocationStatusEffect);
    allocationStatusService = TestBed.inject(AccountAllocationService);
    apiErrorHandlingService = TestBed.inject(ApiErrorHandlingService);
    mockStore = TestBed.inject(MockStore);
    mockAllocationSelector = mockStore.overrideSelector(
      selectAccountId,
      TEST_ACCOUNT_ID
    );
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });

  describe('prepareWizard', () => {
    it('should return the [fetchAllocationStatus, canGoBack] actions with the proper props', () => {
      actions = hot('-a', { a: prepareWizard({ accountId: TEST_ACCOUNT_ID }) });
      const expectedEffectResponse = cold('-(bc)', {
        b: fetchAllocationStatus({ accountId: TEST_ACCOUNT_ID }),
        c: canGoBack({
          goBackRoute: `/account/${TEST_ACCOUNT_ID}`,
          extras: {
            queryParams: {
              selectedSideMenu: MenuItemEnum.ALLOCATION,
            },
          },
        }),
      });
      expect(effects.prepareWizard$).toBeObservable(expectedEffectResponse);
    });
  });

  describe('navigateToAccountAllocation$', () => {
    function test(action: Action) {
      actions = hot('-a', { a: action });
      const expectedEffectResponse = cold('-(bcd)', {
        b: canGoBack({ goBackRoute: null }),
        c: navigateTo({
          route: `/account/${TEST_ACCOUNT_ID}`,
          extras: {
            queryParams: {
              selectedSideMenu: MenuItemEnum.ALLOCATION,
            },
          },
        }),
        d: resetAllocationStatusState(),
      });
      expect(effects.navigateToAccountAllocation$).toBeObservable(
        expectedEffectResponse
      );
    }

    it(`should return the canGoBack and navigateTo actions with proper props on navigateToAccountAllocation dispatch`, () => {
      test(navigateToAccountAllocation());
    });
    it(`should return the canGoBack and navigateTo actions with proper props on cancel dispatch`, () => {
      test(cancel());
    });
  });

  describe('fetchAllocationStatus$', () => {
    it('should return the fetchAllocationStatusSuccess which carries the fetched results', () => {
      const accountAllocationStatus: AccountAllocationStatus = {
        2022: AllocationStatus.WITHHELD,
        2023: AllocationStatus.ALLOWED,
      };
      const fetchAllocationStatusAction = fetchAllocationStatus({
        accountId: TEST_ACCOUNT_ID,
      });
      const fetchAllocationStatusSuccessAction = fetchAllocationStatusSuccess({
        allocationStatus: accountAllocationStatus,
      });
      actions = hot('-a', { a: fetchAllocationStatusAction });
      const serviceResponse = cold('-a|', {
        a: accountAllocationStatus,
      });
      const expectedEffectResponse = cold('--b', {
        b: fetchAllocationStatusSuccessAction,
      });
      allocationStatusService.fetchAllocationStatus = jest.fn(
        () => serviceResponse
      );
      expect(effects.fetchAllocationStatus$).toBeObservable(
        expectedEffectResponse
      );
    });

    /***
     * TODO: Avoid this duplicate by creating a global function for testing errors from effects which call services
     */
    it('should return errors action, with the ErrorSummary transformed by apiErrorHandlingService, on failure', () => {
      const fetchAllocationStatusAction = fetchAllocationStatus({
        accountId: TEST_ACCOUNT_ID,
      });
      const errorSummary = new ErrorSummary([
        { errorMessage: 'test dummy error', componentId: '' },
      ]);
      const errorsAction = errors({ errorSummary });
      apiErrorHandlingService.transform = jest.fn(() => errorSummary);
      const apiErrorHandlingServiceSpy = jest.spyOn(
        apiErrorHandlingService,
        'transform'
      );
      const httpErrorResponse = new HttpErrorResponse({});
      actions = hot('-a', { a: fetchAllocationStatusAction });
      const serviceResponse = cold('-#', {}, httpErrorResponse);
      const expectedEffectResponse = cold('--b', { b: errorsAction });
      allocationStatusService.fetchAllocationStatus = jest.fn(
        () => serviceResponse
      );
      expect(effects.fetchAllocationStatus$).toBeObservable(
        expectedEffectResponse
      );
      expect(apiErrorHandlingServiceSpy).toHaveBeenCalled();
    });
  });

  describe('navigateToVerificationPage$', () => {
    it(`should return the canGoBack and navigateTo action with proper props`, () => {
      const route = [
        '/account',
        TEST_ACCOUNT_ID,
        AllocationStatusRoutePathsModel.ALLOCATION_STATUS,
        AllocationStatusRoutePathsModel.CHECK_UPDATE_REQUEST,
      ].join('/');
      actions = hot('-a', { a: navigateToVerificationPage() });
      const expectedEffectResponse = cold('-(bc)', {
        b: canGoBack({
          goBackRoute: `/account/${TEST_ACCOUNT_ID}/${AllocationStatusRoutePathsModel.ALLOCATION_STATUS}`,
        }),
        c: navigateTo({
          route: route,
        }),
      });
      expect(effects.navigateToVerificationPage$).toBeObservable(
        expectedEffectResponse
      );
    });
  });

  describe('continueToUpdateRequestVerification', () => {
    it('should return the [canGoBack, navigateToVerificationPage] actions with the proper props', () => {
      actions = hot('-a', {
        a: continueToUpdateRequestVerification({
          updateAllocationStatusRequest: {
            changedStatus: {
              2021: AllocationStatus.ALLOWED,
            },
            justification: 'some text here for justification',
          },
        }),
      });
      const expectedEffectResponse = cold('-(bc)', {
        b: canGoBack({
          goBackRoute: `/account/${TEST_ACCOUNT_ID}/${AllocationStatusRoutePathsModel.ALLOCATION_STATUS}`,
        }),
        c: navigateToVerificationPage(),
      });
      expect(effects.continueToUpdateRequestVerification$).toBeObservable(
        expectedEffectResponse
      );
    });
  });

  describe('updateAllocationStatus$', () => {
    it(
      'should call the service and return ' +
        '[updateAllocationStatusSuccess, navigateToAccountAllocation, resetAllocationStatusState]',
      () => {
        const updateRequest: UpdateAllocationStatusRequest = {
          justification: 'test test',
          changedStatus: {
            2021: AllocationStatus.ALLOWED,
          },
        };
        mockStore.overrideSelector(selectUpdateRequest, updateRequest);

        const otherAccountId = '1232132222';
        const serviceResponse = cold('-a|', {
          a: otherAccountId,
        });
        allocationStatusService.updateAllocationStatus = jest.fn(
          (id, request) => {
            expect(id).toBe(TEST_ACCOUNT_ID);
            expect(request).toBe(updateRequest);
            return serviceResponse;
          }
        );

        actions = hot('-a', {
          a: updateAllocationStatus(),
        });

        const expectedEffectResponse = cold('--(bc)', {
          b: updateAllocationStatusSuccess({
            updatedAccountId: otherAccountId,
          }),
          c: navigateToAccountAllocation(),
        });

        expect(effects.updateAllocationStatus$).toBeObservable(
          expectedEffectResponse
        );
      }
    );

    /***
     * TODO: Avoid this duplicate by creating a global function for testing errors from effects which call services
     */
    it('should return errors action, with the ErrorSummary transformed by apiErrorHandlingService, on failure', () => {
      const updateRequest: UpdateAllocationStatusRequest = {
        justification: 'test test',
        changedStatus: {
          2021: AllocationStatus.ALLOWED,
        },
      };
      mockStore.overrideSelector(selectUpdateRequest, updateRequest);
      const errorSummary = new ErrorSummary([
        { errorMessage: 'test dummy error', componentId: '' },
      ]);
      const errorsAction = errors({ errorSummary });
      apiErrorHandlingService.transform = jest.fn(() => errorSummary);
      const apiErrorHandlingServiceSpy = jest.spyOn(
        apiErrorHandlingService,
        'transform'
      );
      const httpErrorResponse = new HttpErrorResponse({});
      actions = hot('-a', {
        a: updateAllocationStatus(),
      });
      const serviceResponse = cold('-#', {}, httpErrorResponse);
      const expectedEffectResponse = cold('--b', { b: errorsAction });
      allocationStatusService.updateAllocationStatus = jest.fn(
        () => serviceResponse
      );
      expect(effects.updateAllocationStatus$).toBeObservable(
        expectedEffectResponse
      );
      expect(apiErrorHandlingServiceSpy).toHaveBeenCalled();
    });
  });

  describe('navigateToCancel$', () => {
    it('should set the can go back url to the current url and navigate to cancel page', () => {
      const route = [
        '/account',
        TEST_ACCOUNT_ID,
        AllocationStatusRoutePathsModel.ALLOCATION_STATUS,
        AllocationStatusRoutePathsModel.CANCEL_UPDATE_REQUEST,
      ].join('/');
      actions = hot('-a', { a: navigateToCancel() });
      const expectedEffectResponse = cold('-(bc)', {
        b: canGoBack({
          goBackRoute: MOCK_CURRENT_URL,
        }),
        c: navigateTo({
          route: route,
        }),
      });
      expect(effects.navigateToCancel$).toBeObservable(expectedEffectResponse);
    });
  });
});
