import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, switchMap, take, tap } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { SystemAdministrationActions } from '../store/actions';
import { ResetDatabaseResult } from '../model';
import { selectResetDatabaseResult } from '../store/reducers';
import { ResetDatabaseComponent } from '../components';
import { clearDatabaseResetResult } from '../store/actions/system-administration.actions';

@Injectable({
  providedIn: 'root',
})
export class SystemAdministrationResetGuard {
  constructor(private store: Store) {}

  canDeactivate(component: ResetDatabaseComponent): boolean {
    this.store.dispatch(clearDatabaseResetResult());
    return true;
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> {
    return this.getResetDatabaseResult().pipe(
      switchMap(() => of(true)),
      catchError(() => of(false))
    );
  }

  private getResetDatabaseResult() {
    return this.store.select(selectResetDatabaseResult).pipe(
      tap((result) => this.prefetch(result)),
      take(1)
    );
  }

  private prefetch(data: ResetDatabaseResult) {
    if (!data) {
      this.store.dispatch(
        SystemAdministrationActions.submitResetDatabaseAction()
      );
    }
  }
}
