import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { take } from 'rxjs/operators';
import { calculateGoBackPathFromSelectAccessRights } from '@authorised-representatives/reducers';

@Injectable()
export class SelectAccessRightsResolver {
  constructor(private store: Store) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<string> {
    return this.store
      .select(calculateGoBackPathFromSelectAccessRights)
      .pipe(take(1));
  }
}
