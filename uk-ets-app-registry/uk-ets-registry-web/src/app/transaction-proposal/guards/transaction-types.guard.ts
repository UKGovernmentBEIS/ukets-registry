import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { TransactionProposalActions } from '@transaction-proposal/actions';
import { selectAllowedTransactionTypes } from '@transaction-proposal/reducers';
import { TransactionTypesResult } from '@shared/model/transaction';
import { getUrlIdentifier } from '@shared/shared.util';

@Injectable({
  providedIn: 'root',
})
export class TransactionTypesGuard {
  constructor(private router: Router, private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const { accountId } = route.params;
    return this.getTransactionTypesResult(getUrlIdentifier(accountId)).pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }

  private getTransactionTypesResult(accountId: string) {
    return this.store.select(selectAllowedTransactionTypes).pipe(
      tap((data) => this.prefetch(accountId, data)),
      filter((data) => {
        return data.result.length > 0 && data.accountId === accountId;
      }),
      take(1)
    );
  }

  private prefetch(accountId: string, data: TransactionTypesResult) {
    if (data.result.length === 0) {
      this.store.dispatch(
        TransactionProposalActions.fetchLoadAndShowAllowedTransactionTypes({
          accountId,
        })
      );
    }
  }
}
