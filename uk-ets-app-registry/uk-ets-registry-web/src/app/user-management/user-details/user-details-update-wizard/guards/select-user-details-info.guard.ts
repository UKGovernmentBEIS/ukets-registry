import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { selectCurrentUserDetailsInfo } from '@user-update/reducers';
import { IUser } from '@shared/user';
import {
  fetchCurrentUserDetailsInfo,
  loadedFromMyProfilePage,
} from '@user-update/action/user-details-update.action';

@Injectable({
  providedIn: 'root',
})
export class SelectUserDetailsInfoGuard {
  constructor(private router: Router, private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const { urid } = route.params;
    return this.getUserDetailsInfo(
      urid,
      route.queryParams['isMyProfilePage']
    ).pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }

  private getUserDetailsInfo(urid: string, isMyProfilePage: boolean) {
    return this.store.select(selectCurrentUserDetailsInfo).pipe(
      tap((data) => this.prefetch(urid, data, isMyProfilePage)),
      filter((data) => data != null),
      take(1)
    );
  }

  private prefetch(urid: string, data: IUser, isMyProfilePage: boolean) {
    if (isMyProfilePage) {
      this.store.dispatch(loadedFromMyProfilePage({ isMyProfilePage }));
    }
    if (data == null) {
      this.store.dispatch(
        fetchCurrentUserDetailsInfo({
          urid,
        })
      );
    }
  }
}
