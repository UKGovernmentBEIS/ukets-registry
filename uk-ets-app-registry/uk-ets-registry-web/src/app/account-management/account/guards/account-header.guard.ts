import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { selectAccount } from '@account-management/account/account-details/account.selector';
import { prepareNavigationToAccount } from '@account-management/account/account-details/account.actions';

import { Observable, of } from 'rxjs';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { Account } from '@shared/model/account';
import { empty, getUrlIdentifier } from '@shared/shared.util';

@Injectable()
export class AccountHeaderGuard {
  constructor(private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const { accountId } = route.params;
    return this.getAccount(getUrlIdentifier(accountId)).pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): Observable<boolean> {
    const { accountId } = currentRoute.params;
    this.store.dispatch(
      prepareNavigationToAccount({ accountId: getUrlIdentifier(accountId) })
    );
    return of(true);
  }

  private getAccount(accountId: string) {
    return this.store.select(selectAccount).pipe(
      tap((data) => this.prefetch(accountId, data)),
      filter(
        (data) =>
          !empty(data.identifier) && data.identifier.toString() === accountId
      ),
      take(1)
    );
  }

  private prefetch(accountId: string, data: Account) {
    /*
     TODO: UKETS-4673 always dispatch prepareNavigationToAccount. When performing a transaction the holdings in the accounts details page are not updated
     We should revisit this, as it would be more proper
     to create an effect to listen to  the TransactionProposalActions.submitProposalSuccess and initiate a fetchAccountHoldings action
     */
    //if (empty(data.identifier) || data.identifier.toString() !== accountId) {
    this.store.dispatch(prepareNavigationToAccount({ accountId: accountId }));
    //}
  }
}
