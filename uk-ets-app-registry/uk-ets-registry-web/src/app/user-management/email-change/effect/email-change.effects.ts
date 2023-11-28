import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import {
  confirmEmailChange,
  loadConfirmation,
  navigateToVerificationPage,
  requestEmailChangeAction
} from '@email-change/action/email-change.actions';
import { catchError, concatMap, switchMap } from 'rxjs/operators';
import { errors } from '@shared/shared.action';
import { ApiErrorHandlingService } from '@shared/services';
import { RequestEmailChangeService } from '@email-change/service/request-email-change.service';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';

@Injectable()
export class EmailChangeEffect {
  constructor(
    private requestEmailChangeService: RequestEmailChangeService,
    private actions$: Actions,
    private store: Store,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}

  changeEmail$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(requestEmailChangeAction),
      concatMap(action =>
        this.requestEmailChangeService.changeEmail(action.request).pipe(
          concatMap(() => {
            return [
              navigateToVerificationPage({
                newEmail: action.request.newEmail
              })
            ];
          }),
          catchError((error: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                )
              })
            );
          })
        )
      )
    );
  });

  confirmEmailChange$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(confirmEmailChange),
      concatMap(action =>
        this.requestEmailChangeService.confirmEmailChange(action.token).pipe(
          concatMap(result => {
            return [
              loadConfirmation({
                urid: result.urid,
                requestId: result.requestId,
                expired: result.tokenExpired
              })
            ];
          }),
          catchError((error: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                )
              })
            );
          })
        )
      )
    );
  });
}
