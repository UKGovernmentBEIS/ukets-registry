import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { selectUserDetails } from '@user-management/user-details/store/reducers';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import {
  fetchAUserDetailsUpdatePendingApproval,
  prepareNavigationToUserDetails,
} from '@user-management/user-details/store/actions';
import { Observable, of } from 'rxjs';
import {
  catchError,
  filter,
  switchMap,
  take,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { empty } from '@shared/shared.util';
import { KeycloakUser } from '@shared/user';
import { selectUrid } from '@registry-web/auth/auth.selector';
import { UserUpdateDetailsType } from '@user-update/model';

@Injectable()
export class UserHeaderGuard {
  constructor(private store: Store) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    const urid = route.paramMap.get('urid');
    return this.getUser(urid).pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }

  private getUser(urid: string) {
    return this.store.select(selectUserDetails).pipe(
      withLatestFrom(this.store.select(selectUrid)),
      tap(([data, currentUserUrid]) =>
        empty(urid)
          ? this.prefetch(currentUserUrid, data)
          : this.prefetch(urid, data)
      ),
      filter(
        ([data, currentUserUrid]) =>
          !empty(data.attributes.urid) &&
          data.attributes.urid[0] === (empty(urid) ? currentUserUrid : urid)
      ),
      take(1)
    );
  }

  private prefetch(urid: string, data: KeycloakUser) {
    this.store.dispatch(
      fetchAUserDetailsUpdatePendingApproval({
        updateType: UserUpdateDetailsType.UPDATE_USER_DETAILS,
        urid: urid,
      })
    );
    //TODO: Shall be revisited with a proper prefetch solution

    // if (empty(data.attributes.urid) || data.attributes.urid[0] !== urid) {
    this.store.dispatch(
      prepareNavigationToUserDetails({ urid, backRoute: null })
    );
    // }
  }
}
