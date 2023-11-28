import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, exhaustMap, map, mergeMap, tap } from 'rxjs/operators';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';
import { User } from '@shared/user';
import * as fromRegistration from './registration.actions';
import {
  deleteRegistration,
  verificationNextStep,
} from './registration.actions';
import {
  RegistrationService,
  UserRepresentation,
} from './registration.service';
import { of } from 'rxjs';
import { ApiErrorHandlingService } from '@shared/services';

@Injectable()
export class RegistrationEffects {
  checkBlacklistedPasswordRequested$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromRegistration.RegistrationActionTypes.CHECK_PASSWORD_REQUESTED),
      mergeMap((action: { password: string }) => {
        return [
          fromRegistration.updateUserPassword({
            password: action.password,
          }),
          fromRegistration.setPasswordSuccess(),
        ];
      })
    )
  );

  navigateToCheckAnswersAndSubmit$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(fromRegistration.RegistrationActionTypes.SET_PASSWORD_SUCCESS),
        tap(() =>
          this.router.navigate(['/registration/check-answers-and-submit'], {
            skipLocationChange: false,
          })
        )
      ),
    { dispatch: false }
  );

  submittedRegistration$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromRegistration.RegistrationActionTypes.SUBMIT),
      exhaustMap((action: { user: User }) =>
        this.registrationService
          .updateUser(action.user.userId, action.user)
          .pipe(
            map((result) =>
              fromRegistration.persistRegistration({ user: result })
            ),
            catchError((httpErrorResponse: HttpErrorResponse) => {
              // TODO: we have to unify the way we return responses from the registration API as we do from the registry API
              // see: ApiErrorBody interface
              const violations: Violation[] =
                httpErrorResponse.error.violations;
              const errorDetails: ErrorDetail[] = [];
              if (violations) {
                violations.forEach((v) => {
                  errorDetails.push({
                    errorMessage: v.message,
                    componentId: '',
                  });
                });
              }
              return of(
                errors({
                  errorSummary: {
                    errors: errorDetails,
                  },
                })
              );
            })
          )
      )
    )
  );

  persistedRegistration$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fromRegistration.RegistrationActionTypes.PERSIST),
      exhaustMap((action: { user: UserRepresentation }) => {
        return this.registrationService.persistUser(action.user).pipe(
          map(() => fromRegistration.completedRegistration()),
          catchError((httpError: HttpErrorResponse) => {
            // eslint-disable-next-line ngrx/no-multiple-actions-in-effects
            return [
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  httpError.error
                ),
              }),
              deleteRegistration(action),
            ];
          })
        );
      })
    );
  });

  deleteRegistration$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromRegistration.deleteRegistration),
      mergeMap((action: { user: UserRepresentation }) => {
        return this.registrationService
          .deleteUser(action.user.id)
          .pipe(map(() => fromRegistration.failedRegistration()));
      })
    )
  );

  completedRegistration$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromRegistration.RegistrationActionTypes.COMPLETED),
      map(() => {
        this.router.navigate(['/registration/registered'], {
          skipLocationChange: false,
        });
        return fromRegistration.cleanUser();
      })
    )
  );

  verifyUserEmail$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fromRegistration.RegistrationActionTypes.VERIFY_USER_EMAIL),
      mergeMap(
        (action: {
          token: string;
          potentialErrors: Map<any, ErrorDetail>;
          nextStepMessages: Map<any, string>;
        }) =>
          this.registrationService.verifyUserEmail(action.token).pipe(
            map((userRepresentation) => {
              return fromRegistration.updateUserRepresentation({
                userRepresentation,
              });
            }),
            catchError((httpError: HttpErrorResponse) => {
              console.log(httpError);
              return action.potentialErrors.has(httpError.status)
                ? [
                    verificationNextStep({
                      message: action.nextStepMessages.get(httpError.status),
                    }),
                    errors({
                      errorSummary: new ErrorSummary(
                        Array.of(action.potentialErrors.get(httpError.status))
                      ),
                    }),
                  ]
                : [
                    verificationNextStep({
                      message: action.nextStepMessages.get('other'),
                    }),
                    errors({
                      errorSummary: new ErrorSummary(
                        Array.of(action.potentialErrors.get('other'))
                      ),
                    }),
                  ];
            })
          )
      )
    )
  );

  constructor(
    private router: Router,
    private actions$: Actions,
    private registrationService: RegistrationService,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}
}
// temporary solution to support backend errors
interface Violation {
  fieldName: string;
  message: string;
}
