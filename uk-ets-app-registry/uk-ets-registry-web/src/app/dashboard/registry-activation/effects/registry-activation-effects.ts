import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  catchError,
  concatMap,
  map,
  mergeMap,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { Router } from '@angular/router';

import { HttpErrorResponse } from '@angular/common/http';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import { EMPTY, of } from 'rxjs';
import { select, Store } from '@ngrx/store';
import {
  retrieveEnrolmentKeyDetails,
  retrieveUser,
} from '@user-management/user-details/store/actions';
import { ApiErrorHandlingService } from '@shared/services';
import { selectEtrAddress } from '@shared/shared.selector';
import { RegistryActivationService } from '../services';
import {
  RegistryActivationActionTypes,
  requestActivationCode,
  submitNewRegistryActivationCodeRequest,
  submitNewRegistryActivationCodeRequestSuccess,
} from '../actions/registry-activation.actions';
import { selectUrid } from '../../../auth/auth.selector';

@Injectable()
export class RegistryActivationEffects {
  constructor(
    private registryActivationService: RegistryActivationService,
    private actions$: Actions,
    private router: Router,
    private store: Store,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}

  enrolUser$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(RegistryActivationActionTypes.ENROL_USER),
        mergeMap((action: { enrolmentKey: string }) => {
          return this.registryActivationService
            .enrolUser(action.enrolmentKey)
            .pipe(
              map(() => {
                this.router.navigate([
                  'dashboard/registry-activation/enrolled',
                ]);
              }),
              catchError((err) => {
                this.handleError(err);
                return of();
              })
            );
        })
      );
    },
    { dispatch: false }
  );

  requestActivationCode$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(requestActivationCode),
      withLatestFrom(this.store.pipe(select(selectUrid))),
      concatMap(([action, currentUrid]) => {
        const generatedAction = { ...action, urid: currentUrid };
        return [
          retrieveUser(generatedAction),
          retrieveEnrolmentKeyDetails(generatedAction),
          canGoBack({ goBackRoute: generatedAction.backRoute }),
        ];
      })
    );
  });

  submitNewRegistryActivationCodeRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitNewRegistryActivationCodeRequest),
      withLatestFrom(this.store.pipe(select(selectEtrAddress))),
      switchMap(([action, email]) => {
        return this.registryActivationService
          .submitNewRegistryActivationCodeRequest()
          .pipe(
            map((requestId) =>
              submitNewRegistryActivationCodeRequestSuccess({ requestId })
            ),
            catchError((err) => {
              this.handleError(err, email);
              return EMPTY;
            })
          );
      })
    );
  });

  navigateToRequestCodeSubmitted$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(submitNewRegistryActivationCodeRequestSuccess),
        tap((action) =>
          this.router.navigate(
            ['dashboard/registry-activation/request-submitted'],
            {
              skipLocationChange: false,
            }
          )
        )
      ),
    { dispatch: false }
  );

  private handleError(err: HttpErrorResponse, param?: string) {
    let errorDetail: ErrorDetail;
    if (err.status === 410) {
      errorDetail = new ErrorDetail(
        null,
        'The registry activation code has expired'
      );
    } else if (err.status === 400) {
      if (
        err?.error?.errorDetails[0]?.message === 'ENROLMENT_KEY_TASK_PENDING'
      ) {
        errorDetail = new ErrorDetail(
          null,
          `The registry administrator is already processing your Registry Activation Code. You 'll receive your Registry Activation Code in a separate email as soon as possible`
        );
      } else {
        errorDetail = new ErrorDetail(
          null,
          'The registry activation code that you provided is invalid'
        );
      }
    } else {
      errorDetail = new ErrorDetail(
        null,
        'Unexpected error on submitting the registry activation code'
      );
    }
    this.store.dispatch(
      errors({ errorSummary: new ErrorSummary([errorDetail]) })
    );
  }
}
