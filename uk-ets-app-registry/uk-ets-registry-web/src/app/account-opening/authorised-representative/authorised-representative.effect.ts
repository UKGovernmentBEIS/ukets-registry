import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import {
  AuthorisedRepresentativeActionTypes,
  FetchARList,
  FetchOneAR,
  loadAuthorisedRepresentatives,
  setCurrentAuthorisedRepresentative,
} from './authorised-representative.actions';
import { AuthorisedRepresentativeService } from './authorised-representative.service';
import { errors } from '@shared/shared.action';
import { AuthorisedRepresentativeWizardRoutes } from './authorised-representative-wizard-properties';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { ApiErrorHandlingService } from '@shared/services';
import { empty } from '@shared/shared.util';

@Injectable()
export class AuthorisedRepresentativeEffects {
  fetchAuthorisedRepresentatives$ = createEffect(() =>
    this.actions$.pipe(
      ofType<FetchARList>(AuthorisedRepresentativeActionTypes.FETCH_LIST),
      mergeMap((action) =>
        this.arService
          .getAuthorisedRepresentatives(action.accountHolderId)
          .pipe(
            map((result) => loadAuthorisedRepresentatives({ ARs: result })),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          )
      )
    )
  );

  fetchAuthorisedRepresentative$ = createEffect(() =>
    this.actions$.pipe(
      ofType<FetchOneAR>(AuthorisedRepresentativeActionTypes.FETCH_ONE),
      switchMap((action) => {
        return this.arService.getAuthorisedRepresentative(action.urid).pipe(
          map((result) => {
            if (result !== undefined) {
              this.router.navigate(
                [AuthorisedRepresentativeWizardRoutes.ACCESS_RIGHTS],
                { skipLocationChange: true }
              );
              return setCurrentAuthorisedRepresentative({ AR: result });
            }
          }),
          catchError((error: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                ),
              })
            );
          })
        );
      })
    )
  );

  constructor(
    private router: Router,
    private actions$: Actions,
    private arService: AuthorisedRepresentativeService,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}
}
