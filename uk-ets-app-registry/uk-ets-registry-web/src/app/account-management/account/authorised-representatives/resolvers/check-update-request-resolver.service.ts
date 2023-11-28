import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { take } from 'rxjs/operators';
import { calculateGoBackPathFromCheckUpdateRequest } from '@authorised-representatives/reducers';

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
