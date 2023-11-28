import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { errors } from '@shared/shared.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { of } from 'rxjs';
import { SystemAdministrationService } from '../../services/system-administration.service';
import { SystemAdministrationActions } from '../actions';
import { submitResetDatabaseSuccess } from '../actions/system-administration.actions';

@Injectable()
export class SystemAdministrationEffects {
  constructor(
    private systemAdministrationService: SystemAdministrationService,
    private actions$: Actions,
    private store: Store,
    private router: Router
  ) {}

  submitResetDatabaseAction$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(SystemAdministrationActions.submitResetDatabaseAction),
      mergeMap(() => {
        return this.systemAdministrationService.reset().pipe(
          map(result => submitResetDatabaseSuccess({ result })),
          catchError((httpError: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: SystemAdministrationEffects.translateError(
                  httpError
                )
              })
            );
          })
        );
      })
    );
  });

  private static translateError(
    httpErrorResponse: HttpErrorResponse
  ): ErrorSummary {
    let message = '';
    if (!httpErrorResponse.status) {
      message =
        'You do not have system administrator privileges to reset the database.';
    } else if (httpErrorResponse.error.errors) {
      for (const springError of httpErrorResponse.error.errors) {
        message += springError.defaultMessage;
      }
    } else {
      message = JSON.stringify(httpErrorResponse);
    }
    return {
      errors: [{ componentId: '', errorMessage: message }]
    };
  }

  handleHttpError(httpError: HttpErrorResponse, urid?: string) {
    let errorDetails: ErrorDetail[];
    if (httpError.status === 404) {
      errorDetails = [];
      errorDetails.push(
        new ErrorDetail(
          null,
          'There are no allowed system administration actions for this user'
        )
      );
    }

    this.store.dispatch(errors({ errorSummary: { errors: errorDetails } }));
    this.router.navigate([`/dashboard`]);
  }
}
