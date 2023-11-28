import * as AlertsActionsTypes from '@registry-web/dashboard/alert-monitoring/actions/alerts.actions';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, switchMap, withLatestFrom } from 'rxjs/operators';
import { selectLoggedInUser } from '@registry-web/auth/auth.selector';
import { of } from 'rxjs';
import { UserDetailService } from '@user-management/service';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AlertsService } from '@registry-web/dashboard/alert-monitoring/service/alerts.service';
import { AuthApiService } from '@registry-web/auth/auth-api.service';

@Injectable({
  providedIn: 'root',
})
export class AlertsEffect {
  getAlerts$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AlertsActionsTypes.getAlerts),
      withLatestFrom(this.store.select(selectLoggedInUser)),
      switchMap(([, loggedInUser]) => {
        return this.alertsService.getAlerts(loggedInUser.urid).pipe(
          map((alerts) => {
            return AlertsActionsTypes.retrieveAlertSuccess({ alerts });
          }),
          catchError((err) => of(AlertsActionsTypes.retrieveAlertError(err)))
        );
      })
    )
  );

  constructor(
    private userDetailService: UserDetailService,
    private alertsService: AlertsService,
    private authApiService: AuthApiService,
    private actions$: Actions,
    private store: Store
  ) {}
}
