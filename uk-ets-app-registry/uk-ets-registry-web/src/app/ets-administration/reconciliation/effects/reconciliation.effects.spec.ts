import { Observable } from 'rxjs';
import { ReconciliationEffects } from '@reconciliation-administration/effects/reconciliation.effects';
import { ReconciliationService } from '@reconciliation-administration/service/reconciliation.service';
import { provideMockStore } from '@ngrx/store/testing';
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { ApiErrorHandlingService } from '@shared/services';
import { Reconciliation } from '@shared/model/reconciliation-model';
import {
  fetchLastStartedReconciliation,
  startReconciliation,
  updateLatestReconciliation,
} from '@reconciliation-administration/actions/reconciliation.actions';
import { cold, hot } from 'jasmine-marbles';
import { ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';
import { HttpErrorResponse } from '@angular/common/http';

describe('ReconciliationEffects', () => {
  let actions: Observable<any>;
  let effects: ReconciliationEffects;
  let reconciliationService: ReconciliationService;
  let apiErrorHandlingService: ApiErrorHandlingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ReconciliationEffects,
        provideMockStore(),
        provideMockActions(() => actions),
        ApiErrorHandlingService,
        {
          provide: ReconciliationService,
          useValue: {
            fetchLatestReconciliation: jest.fn(),
            startReconciliation: jest.fn(),
          },
        },
      ],
    });
    effects = TestBed.inject(ReconciliationEffects);
    reconciliationService = TestBed.inject(ReconciliationService);
    apiErrorHandlingService = TestBed.inject(ApiErrorHandlingService);
  });

  it('should be created', () => {
    expect(effects).toBeTruthy();
  });

  describe('fetchLatestReconciliation$', () => {
    // Happy path
    it('should return the updateLatestReconciliation action that carries the last reconciliation info', () => {
      // given
      const getLatestReconciliationAction = fetchLastStartedReconciliation();
      actions = hot('-a', { a: getLatestReconciliationAction });
      const latestReconciliation: Reconciliation = {
        identifier: 2341,
        created: '10/12/2019 12:30 UTC',
        updated: '10/12/2019 12:30 UTC',
        status: 'COMPLETED',
      };
      const serviceResponse = cold('-a|', {
        a: latestReconciliation,
      });
      reconciliationService.fetchLatestReconciliation = jest.fn(
        () => serviceResponse
      );
      const updateLatestReconciliationAction = updateLatestReconciliation({
        reconciliation: latestReconciliation,
      });
      const expectedEffectResponse = cold('--b', {
        b: updateLatestReconciliationAction,
      });

      // when then
      expect(effects.fetchLatestReconciliation$).toBeObservable(
        expectedEffectResponse
      );
    });
    // Error during service call
    it('should return errors action, with the ErrorSummary transformed by apiErrorHandlingService, on failure', () => {
      // given
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
      actions = hot('-a', { a: fetchLastStartedReconciliation });
      const serviceResponse = cold('-#', {}, httpErrorResponse);
      reconciliationService.fetchLatestReconciliation = jest.fn(
        () => serviceResponse
      );
      const expectedEffectResponse = cold('--b', { b: errorsAction });
      // when then
      expect(effects.fetchLatestReconciliation$).toBeObservable(
        expectedEffectResponse
      );
      expect(apiErrorHandlingServiceSpy).toHaveBeenCalled();
    });
  });

  describe('startReconciliation', () => {
    // Happy path
    it('should return the started reconciliation that carries the last reconciliation info', () => {
      // given
      const startReconciliationAction = startReconciliation();
      actions = hot('-a', { a: startReconciliationAction });
      const recentlyStartedReconciliation: Reconciliation = {
        identifier: 2341,
        created: '10/12/2019 12:30 UTC',
        updated: '10/12/2019 12:30 UTC',
        status: 'COMPLETED',
      };
      const serviceResponse = cold('-a|', {
        a: recentlyStartedReconciliation,
      });
      reconciliationService.startReconciliation = jest.fn(
        () => serviceResponse
      );
      const updateLatestReconciliationAction = updateLatestReconciliation({
        reconciliation: recentlyStartedReconciliation,
      });
      const expectedEffectResponse = cold('--b', {
        b: updateLatestReconciliationAction,
      });

      // when then
      expect(effects.startReconciliation$).toBeObservable(
        expectedEffectResponse
      );
    });

    // Error during service call
    it('should return errors action, with the ErrorSummary transformed by apiErrorHandlingService, on failure', () => {
      // given
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
      actions = hot('-a', { a: startReconciliation });
      const serviceResponse = cold('-#', {}, httpErrorResponse);
      reconciliationService.startReconciliation = jest.fn(
        () => serviceResponse
      );
      const expectedEffectResponse = cold('--b', { b: errorsAction });
      // when then
      expect(effects.startReconciliation$).toBeObservable(
        expectedEffectResponse
      );
      expect(apiErrorHandlingServiceSpy).toHaveBeenCalled();
    });
  });
});
