import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { selectCalculateGoBackPathFromCheckUpdateRequest } from '@account-management/account/account-holder-details-wizard/reducers';

@Injectable()
export class CheckUpdateRequestResolver {
  constructor(private store: Store) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<string> {
    return this.store
      .select(selectCalculateGoBackPathFromCheckUpdateRequest)
      .pipe(take(1));
  }
}
