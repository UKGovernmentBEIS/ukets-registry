import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { calculateGoBackPathFromCheckUpdateRequest } from '@account-management/account/account-holder-details-wizard/reducers';

@Injectable()
export class CheckUpdateRequestResolver {
  constructor(private store: Store) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<string> {
    return this.store
      .select(calculateGoBackPathFromCheckUpdateRequest)
      .pipe(take(1));
  }
}
