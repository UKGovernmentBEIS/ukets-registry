import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { calculateGoBackPathFromFinalPage } from '@account-management/account/trusted-account-list/reducers';
import { take } from 'rxjs/operators';

@Injectable()
export class CheckUpdateRequestResolver {
  constructor(private store: Store) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<string> {
    return this.store.select(calculateGoBackPathFromFinalPage).pipe(take(1));
  }
}
