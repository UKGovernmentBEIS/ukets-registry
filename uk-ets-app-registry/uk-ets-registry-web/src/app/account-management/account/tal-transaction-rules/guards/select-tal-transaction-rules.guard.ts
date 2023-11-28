import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { selectCurrentRules } from '@tal-transaction-rules/reducers';
import { Store } from '@ngrx/store';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { TrustedAccountListRules } from '@shared/model/account';
import { TalTransactionRulesActions } from '@tal-transaction-rules/actions';

@Injectable({
  providedIn: 'root',
})
export class SelectTalTransactionRulesGuard {
  constructor(private router: Router, private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const { accountId } = route.params;
    return this.getTalTransactionRulesResult(accountId).pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }

  private getTalTransactionRulesResult(accountId: string) {
    return this.store.select(selectCurrentRules).pipe(
      tap((data) => this.prefetch(accountId, data)),
      filter((data) => data != null),
      take(1)
    );
  }

  private prefetch(accountId: string, data: TrustedAccountListRules) {
    if (data == null) {
      this.store.dispatch(
        TalTransactionRulesActions.fetchCurrentRules({
          accountId,
        })
      );
    }
  }
}
