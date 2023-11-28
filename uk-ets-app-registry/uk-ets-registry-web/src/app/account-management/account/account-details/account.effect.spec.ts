import { Observable } from 'rxjs';
import { AccountEffect } from '@account-management/account/account-details/account.effect';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { ApiErrorHandlingService } from '@shared/services';
import { AccountAllocation } from '@shared/model/account/account-allocation';
import {
  fetchAccountAllocation,
  fetchAccountAllocationSuccess,
} from '@account-management/account/account-details/account.actions';
import { cold, hot } from 'jasmine-marbles';
import { ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';
import { HttpErrorResponse } from '@angular/common/http';
import { AccountApiService } from '@account-management/service/account-api.service';
import { AccountAllocationService } from '@account-management/service/account-allocation.service';
import { ExportFileService } from '@shared/export-file/export-file.service';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { TaskService } from '@shared/services/task-service';
import { AccountComplianceService } from '@account-management/account/account-details/services';

describe('AccountEfects', () => {
  let actions: Observable<any>;
  let effects: AccountEffect;
  let allocationService: AccountAllocationService;
  let complianceService: AccountComplianceService;
  let apiErrorHandlingService: ApiErrorHandlingService;
  let exportFileService: ExportFileService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([])],

      providers: [
        AccountEffect,
        ApiErrorHandlingService,
        ExportFileService,

        provideMockStore(),
        provideMockActions(() => actions),
        {
          provide: AccountApiService,
          useValue: {
            getFiltersPermissions: jest.fn(),
            fetchAccount: jest.fn(),
            fetchAccountHoldings: jest.fn(),
          },
        },
        {
          provide: AccountAllocationService,
          useValue: {
            fetchAllocation: jest.fn(),
          },
        },
        {
          provide: AccountComplianceService,
          useValue: {
            fetchAllocation: jest.fn(),
          },
        },
        {
          provide: TaskService,
          useValue: {
            checkTaskPendingApproval: jest.fn(),
          },
        },
      ],
    });

    effects = TestBed.inject(AccountEffect);
    allocationService = TestBed.inject(AccountAllocationService);
    complianceService = TestBed.inject(AccountComplianceService);
    apiErrorHandlingService = TestBed.inject(ApiErrorHandlingService);
    exportFileService = TestBed.inject(ExportFileService);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });

  describe('fetchAccountAllocation', () => {
    it('should return the loadAccountAllocation action, with the fetched allocation, on success', () => {
      const accountId = '123213';
      const allocation: AccountAllocation = {
        standard: null,
        underNewEntrantsReserve: null,
      };
      const fetchAccountAllocationAction = fetchAccountAllocation({
        accountId,
      });
      const loadAccountAllocationAction = fetchAccountAllocationSuccess({
        allocation,
      });

      actions = hot('-a', { a: fetchAccountAllocationAction });
      const serviceResponse = cold('-a|', { a: allocation });
      const expectedEffectResponse = cold('--b', {
        b: loadAccountAllocationAction,
      });
      allocationService.fetchAllocation = jest.fn(() => serviceResponse);

      expect(effects.fetchAccountAllocation$).toBeObservable(
        expectedEffectResponse
      );
    });

    it('should return errors action, with the ErrorSummary transformed by apiErrorHandlingService, on failure', () => {
      const accountId = '123213';
      const fetchAccountAllocationAction = fetchAccountAllocation({
        accountId,
      });
      const errorSummary = new ErrorSummary([
        {
          errorMessage: 'test dummy error',
          componentId: '',
        },
      ]);
      const errorsAction = errors({ errorSummary });

      apiErrorHandlingService.transform = jest.fn(() => errorSummary);
      const apiErrorHandlingServiceSpy = jest.spyOn(
        apiErrorHandlingService,
        'transform'
      );
      const httpErrorResponse = new HttpErrorResponse({});

      actions = hot('-a', { a: fetchAccountAllocationAction });
      const serviceResponse = cold('-#', {}, httpErrorResponse);
      const expectedEffectResponse = cold('--b', { b: errorsAction });
      allocationService.fetchAllocation = jest.fn(() => serviceResponse);
      expect(effects.fetchAccountAllocation$).toBeObservable(
        expectedEffectResponse
      );
      expect(apiErrorHandlingServiceSpy).toHaveBeenCalled();
    });
  });
});
