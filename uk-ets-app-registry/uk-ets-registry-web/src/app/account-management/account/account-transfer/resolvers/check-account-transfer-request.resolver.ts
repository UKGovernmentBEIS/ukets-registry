import { Injectable } from '@angular/core';
import { RouterStateSnapshot, ActivatedRouteSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { calculateGoBackPathFromCheckAccountTransferRequest } from '@account-transfer/store/reducers';

@Injectable({
  providedIn: 'root',
})
export class CheckAccountTransferRequestResolver {
  constructor(private store: Store) {}

  resolve(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<string> {
    return this.store
      .select(calculateGoBackPathFromCheckAccountTransferRequest)
      .pipe(take(1));
  }
}
