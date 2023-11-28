import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';

import { Store } from '@ngrx/store';
import { TransactionDetailsActions } from '@transaction-management/transaction-details/actions';
import { TransactionDetails } from '@transaction-management/model';
import { selectTransaction } from '@transaction-management/transaction-details/transaction-details.selector';
import { empty } from '@shared/shared.util';

@Injectable({
  providedIn: 'root',
})
export class TransactionHeaderGuard {
  constructor(private router: Router, private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const { transactionIdentifier } = route.params;
    return this.getTransaction(transactionIdentifier).pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }

  private getTransaction(identifier: string) {
    return this.store.select(selectTransaction).pipe(
      tap((data) => this.prefetch(identifier, data)),
      filter(
        (data) => !empty(data.identifier) && data.identifier === identifier
      ),
      take(1)
    );
  }

  private prefetch(transactionIdentifier: string, data: TransactionDetails) {
    // if (
    //   empty(data.identifier) ||
    //   data.identifier !== transactionIdentifier ||
    //   data.type === 'SurrenderAllowances' ||
    //   data.type === 'AllocateAllowances' ||
    //   data.type === 'DeletionOfAllowances'
    // ) {
    this.store.dispatch(
      TransactionDetailsActions.fetchTransaction({
        transactionIdentifier: transactionIdentifier,
      })
    );
  }
  // }
}
