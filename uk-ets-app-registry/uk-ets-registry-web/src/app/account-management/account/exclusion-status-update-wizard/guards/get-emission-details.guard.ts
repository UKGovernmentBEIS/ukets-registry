import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, of } from 'rxjs';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { fetchCurrentAccountEmissionDetailsInfo } from '@registry-web/account-management/account/exclusion-status-update-wizard/actions/update-exclusion-status.action';
import { selectCurrentAccountEmissionDetails } from '../reducers/update-exclusion-status.selector';
import { VerifiedEmissions } from '@registry-web/account-shared/model';
import { selectCompliantEntityIdentifier } from '../../account-details/store/reducers/account-compliance.selector';

@Injectable({
  providedIn: 'root',
})
export class GetEmissionDetailsGuard {
  constructor(private router: Router, private store: Store) {}
  compliantEntityIdentifier: number;

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    this.store
      .select(selectCompliantEntityIdentifier)
      .subscribe(
        (compliantEntityIdentifier) =>
          (this.compliantEntityIdentifier = compliantEntityIdentifier)
      );
    return this.getEmissionDetails(this.compliantEntityIdentifier).pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }
  getCompliantEntityAndEmissionDetails() {
    return this.store
      .select(selectCompliantEntityIdentifier)
      .pipe(tap((data) => this.getEmissionDetails(data)));
  }

  private getEmissionDetails(compliantEntityIdentifier: number) {
    return this.store.select(selectCurrentAccountEmissionDetails).pipe(
      tap((data) => this.prefetch(compliantEntityIdentifier, data)),
      filter((data) => data != null),
      take(1)
    );
  }

  private prefetch(
    compliantEntityIdentifier: number,
    data: VerifiedEmissions[]
  ) {
    if (data == null) {
      this.store.dispatch(
        fetchCurrentAccountEmissionDetailsInfo({
          compliantEntityIdentifier,
        })
      );
    }
  }
}
