import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { paymentConfirmationGuard } from './payment-confirmation.guard';
import { MockStore, provideMockStore } from '@ngrx/store/testing';
import { cold } from 'jasmine-marbles';

describe('paymentConfirmationGuard', () => {
  let store: MockStore;

  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() =>
      paymentConfirmationGuard(...guardParameters)
    );

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideMockStore({}),
        // other providers
      ],
    });
    store = TestBed.inject(MockStore);
    spyOn(store, 'dispatch').and.callThrough();
    spyOn(store, 'select').and.callThrough();
  });

  test('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });

  test('should return true when paymentStatus contains SUCCESS and paymentMethod is not BACS', () => {
    const response = {
      taskDetailsDTO: {
        paymentStatus: 'PAYMENT_SUCCESS',
        paymentMethod: 'CARD',
      },
      requestIdentifier: 'req-success-1',
    };

    // store.select should emit the response immediately
    (store.select as jasmine.Spy).and.returnValue(
      cold('(a|)', { a: response })
    );

    const expected = cold('(a|)', { a: true });

    const result$ = executeGuard({ url: [{ path: '123' }] } as any, {} as any);
    expect(result$).toBeObservable(expected);

    // first dispatch should be the fetch action with requestId '123'
    const firstDispatchArg = (store.dispatch as jasmine.Spy).calls.argsFor(
      0
    )[0];
    expect(firstDispatchArg.requestId).toBe('123');
  });

  test('should return true when paymentMethod is BACS and paymentStatus contains SUBMITTED', () => {
    const response = {
      taskDetailsDTO: {
        paymentStatus: 'SUBMITTED_BY_BANK',
        paymentMethod: 'BACS',
      },
      requestIdentifier: 'req-bacs-1',
    };

    (store.select as jasmine.Spy).and.returnValue(
      cold('(a|)', { a: response })
    );

    const expected = cold('(a|)', { a: true });

    expect(
      executeGuard({ url: [{ path: 'bacs-id' }] } as any, {} as any)
    ).toBeObservable(expected);
  });

  test('should dispatch navigation actions and return false for non-success/submitted payments', () => {
    const response = {
      taskDetailsDTO: {
        paymentStatus: 'FAILED',
        paymentMethod: 'CARD',
      },
      requestIdentifier: 'req-fail-1',
    };

    (store.select as jasmine.Spy).and.returnValue(
      cold('(a|)', { a: response })
    );

    const expected = cold('(a|)', { a: false });

    expect(
      executeGuard({ url: [{ path: 'fail-id' }] } as any, {} as any)
    ).toBeObservable(expected);

    // Expect dispatch called: 1) fetch, 2) canGoBackToList, 3) prepareNavigationToTask, 4) navigateToTaskDetails
    expect(
      (store.dispatch as jasmine.Spy).calls.count()
    ).toBeGreaterThanOrEqual(4);
  });
});
