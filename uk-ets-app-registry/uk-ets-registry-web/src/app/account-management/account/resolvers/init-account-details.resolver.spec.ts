import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, of, throwError } from 'rxjs';

import { initAccountDetailsResolver } from '@account-management/account/resolvers/init-account-details.resolver';
import { prepareNavigationToAccount } from '@account-management/account/account-details/account.actions';
import {
  areAccountDetailsLoaded,
  selectAccount,
} from '@account-management/account/account-details/account.selector';
import { errors } from '@shared/shared.action';
import { provideMockStore } from '@ngrx/store/testing';

describe('initAccountDetailsResolver', () => {
  let store;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideMockStore()],
    });

    store = TestBed.inject(Store);
    spyOn(store, 'dispatch');
    spyOn(store, 'select');
  });

  const route = {
    params: {
      accountId: '123',
    },
  } as unknown as ActivatedRouteSnapshot;

  const state = {} as RouterStateSnapshot;

  it('should dispatch prepareNavigationToAccount and resolve true when account is loaded', (done) => {
    store.select.and.callFake((selector: any) => {
      if (selector === selectAccount) {
        return of({
          identifier: 123,
        });
      }

      if (selector === areAccountDetailsLoaded) {
        return of(true);
      }

      return of(undefined);
    });

    TestBed.runInInjectionContext(() => {
      (
        initAccountDetailsResolver(route, state) as Observable<boolean>
      ).subscribe((result) => {
        expect(result).toBeTruthy();

        expect(store.dispatch).toHaveBeenCalledWith(
          prepareNavigationToAccount({
            accountId: '123',
          })
        );

        done();
      });
    });
  });

  it('should dispatch errors action and resolve false when selectAccount fails', (done) => {
    const backendError = {
      error: {
        errors: ['Something went wrong'],
      },
    };

    store.select.and.callFake((selector: any) => {
      if (selector === selectAccount) {
        return throwError(() => backendError);
      }

      return of(undefined);
    });

    TestBed.runInInjectionContext(() => {
      (
        initAccountDetailsResolver(route, state) as Observable<boolean>
      ).subscribe((result) => {
        expect(result).toBeFalsy();

        expect(store.dispatch).toHaveBeenCalledWith(
          prepareNavigationToAccount({
            accountId: '123',
          })
        );

        expect(store.dispatch).toHaveBeenCalledWith(
          errors({
            errorSummary: {
              errors: ['Something went wrong'],
            },
          })
        );

        done();
      });
    });
  });

  it('should ignore accounts with a different identifier', () => {
    store.select.and.callFake((selector: any) => {
      if (selector === selectAccount) {
        return of({
          identifier: 999,
        });
      }

      return of(true);
    });

    TestBed.runInInjectionContext(() => {
      const result$ = initAccountDetailsResolver(
        route,
        state
      ) as Observable<boolean>;

      let emitted = false;

      result$.subscribe(() => {
        emitted = true;
      });

      expect(emitted).toBeFalsy();
    });
  });
});
