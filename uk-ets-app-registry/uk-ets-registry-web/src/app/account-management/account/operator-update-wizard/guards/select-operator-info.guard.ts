import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { Operator } from '@shared/model/account';
import { selectCurrentOperatorInfo } from '@operator-update/reducers';
import { OperatorUpdateActions } from '@operator-update/actions';

@Injectable({
  providedIn: 'root',
})
export class SelectOperatorInfoGuard {
  constructor(private router: Router, private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const { accountId } = route.params;
    return this.getOperatorInfo(accountId).pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }

  private getOperatorInfo(accountId: string) {
    return this.store.select(selectCurrentOperatorInfo).pipe(
      tap((data) => this.prefetch(accountId, data)),
      filter((data) => data != null),
      take(1)
    );
  }

  private prefetch(accountId: string, data: Operator) {
    if (data == null) {
      this.store.dispatch(
        OperatorUpdateActions.fetchCurrentOperatorInfo({
          accountId,
        })
      );
    }
  }
}
