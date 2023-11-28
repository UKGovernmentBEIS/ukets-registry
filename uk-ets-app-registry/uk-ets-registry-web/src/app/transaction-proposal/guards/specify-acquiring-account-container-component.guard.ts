import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { SpecifyAcquiringAccountActions } from '@transaction-proposal/actions';
import { trustedAccountsResult } from '@transaction-proposal/reducers';
import { CandidateAcquiringAccounts } from '@shared/model/transaction';

@Injectable({
  providedIn: 'root',
})
export class SpecifyAcquiringAccountContainerComponentGuard {
  constructor(private router: Router, private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const { accountId } = route.params;
    return this.getTrustedAccounts(+accountId).pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }

  private getTrustedAccounts(accountId: number) {
    return this.store.select(trustedAccountsResult).pipe(
      tap((data) => this.prefetch(accountId, data)),
      filter((data) => data.accountId && data.accountId === accountId),
      take(1)
    );
  }

  private prefetch(accountId: number, data: CandidateAcquiringAccounts) {
    if (!data.accountId) {
      this.store.dispatch(
        SpecifyAcquiringAccountActions.getTrustedAccounts({
          accountId,
        })
      );
    }
  }
}
