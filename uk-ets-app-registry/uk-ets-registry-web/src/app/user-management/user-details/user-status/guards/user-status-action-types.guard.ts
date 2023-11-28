import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { selectAllowedUserStatusActions } from '@user-management/user-details/user-status/store/reducers';
import { Store } from '@ngrx/store';
import { UserStatusActions } from '@user-management/user-details/user-status/store/actions';
import { canGoBack } from '@shared/shared.action';
import { UserStatusActionOption } from '@registry-web/user-management/model';

@Injectable({
  providedIn: 'root',
})
export class UserStatusActionTypesGuard {
  constructor(private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const { urid } = route.params;
    return this.getUserStatusActionTypes(urid).pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }

  canDeactivate(): boolean {
    this.store.dispatch(
      canGoBack({
        goBackRoute: null,
      })
    );
    this.store.dispatch(UserStatusActions.clearUserStatus());
    return true;
  }

  private getUserStatusActionTypes(urid: string) {
    return this.store.select(selectAllowedUserStatusActions).pipe(
      tap((data) => this.prefetch(urid, data)),
      filter((data) => {
        return data && data.length > 0;
      }),
      take(1)
    );
  }

  private prefetch(urid: string, data: UserStatusActionOption[]) {
    if (!data || data.length === 0) {
      this.store.dispatch(
        UserStatusActions.fetchLoadAndShowAllowedUserStatusActions({ urid })
      );
    }
  }
}
