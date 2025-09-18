import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import {
  catchError,
  concatMap,
  map,
  mergeMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { errors } from '@shared/shared.action';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { of } from 'rxjs';
import { UserDetailService } from '@user-management/service';
import { selectUrid } from '@user-management/user-details/store/reducers';
import { UserStatusActions } from '../actions';
import {
  clearUserStatus,
  fetchAllowedUserStatusActionsSuccess,
  loadAllowedUserStatusActions,
  navigateTo,
  setComment,
  setSelectedUserStatusAction,
  submitUserStatusActionSuccess,
} from '../actions/user-status.actions';
import { selectUserStatusAction } from '../reducers';
import * as UserDetailsActions from '@user-management/user-details/store/actions';

@Injectable()
export class UserStatusEffects {
  constructor(
    private userApiService: UserDetailService,
    private actions$: Actions,
    private store: Store,
    private router: Router
  ) {}

  fetchLoadAndShowAllowedUserStatusActions$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserStatusActions.fetchLoadAndShowAllowedUserStatusActions),
      mergeMap((action) => {
        return this.userApiService
          .getAllowedUserStatusActions(action.urid)
          .pipe(
            map((result) =>
              fetchAllowedUserStatusActionsSuccess({
                changeUserStatusActionTypes: result,
              })
            ),
            catchError((httpError: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: UserStatusEffects.translateError(httpError),
                })
              );
            })
          );
      })
    );
  });

  fetchAllowedUserStatusActionsSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserStatusActions.fetchAllowedUserStatusActionsSuccess),
      concatLatestFrom(() => this.store.select(selectUrid)),
      mergeMap(([action, urid]) => [
        loadAllowedUserStatusActions({
          changeUserStatusActionTypes: action.changeUserStatusActionTypes,
        }),
        navigateTo({ route: `/user-details/${urid}/status` }),
      ])
    );
  });

  setSelectedUserStatusAndNavigateToConfirmAction$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserStatusActions.setSelectedUserStatusAndNavigateToConfirmAction),
      concatLatestFrom(() => this.store.select(selectUrid)),
      concatMap(([action, urid]) => [
        setSelectedUserStatusAction(action),
        navigateTo({ route: `/user-details/${urid}/status/confirm` }),
      ])
    );
  });

  checkIfUserSuspendedByTheSystem$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserStatusActions.checkIfUserSuspendedByTheSystem),
      concatLatestFrom(() => this.store.select(selectUrid)),
      concatMap(([action, urid]) => {
        return this.userApiService.checkIfUserSuspendedByTheSystem(urid).pipe(
          map((result) =>
            UserStatusActions.checkIfUserSuspendedByTheSystemSuccess({
              userSuspendedByTheSystem: result,
            })
          ),
          catchError((err) => {
            return of(
              UserStatusActions.checkIfUserSuspendedByTheSystemError(err)
            );
          })
        );
      })
    );
  });
  submitUserStatusAction$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserStatusActions.submitUserStatusAction),
      withLatestFrom(
        this.store.select(selectUrid),
        this.store.select(selectUserStatusAction)
      ),
      mergeMap(([action, urid, userStatusAction]) => {
        const commentValue = ['RESTORE', 'SUSPEND'].includes(
          userStatusAction?.value
        )
          ? action.comment
          : userStatusAction?.value;
        return this.userApiService
          .changeUserStatus({
            urid,
            status: userStatusAction.newStatus,
            comment: commentValue,
          })
          .pipe(
            map((result) =>
              submitUserStatusActionSuccess({
                newStatus: result.userStatus,
                comment: commentValue,
                user: result.user,
              })
            ),
            catchError((httpError: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: UserStatusEffects.translateError(httpError),
                })
              );
            })
          );
      })
    );
  });

  submitUserStatusActionSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserStatusActions.submitUserStatusActionSuccess),
      concatLatestFrom(() => this.store.select(selectUrid)),
      mergeMap(([action, urid]) => {
        return [
          clearUserStatus(),
          setComment({
            comment: action.comment,
          }),
          UserDetailsActions.retrieveUserSuccess({ user: action.user }),
          navigateTo({ route: `/user-details/${urid}` }),
        ];
      })
    );
  });

  cancelChangeUserStatus$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserStatusActions.cancelUserStatus),
      concatLatestFrom(() => this.store.select(selectUrid)),
      mergeMap(([, urid]) => [
        clearUserStatus(),
        navigateTo({ route: `/user-details/${urid}` }),
      ])
    );
  });

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(UserStatusActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route]);
        })
      );
    },
    { dispatch: false }
  );

  private static translateError(
    httpErrorResponse: HttpErrorResponse
  ): ErrorSummary {
    let message = '';
    if (!httpErrorResponse.status) {
      message = 'You do not have administrator privileges to update users.';
    } else if (httpErrorResponse.error.errorDetails) {
      message = httpErrorResponse.error.errorDetails[0].message;
    } else if (httpErrorResponse.error.error.message) {
      message = httpErrorResponse.error.error.message;
    } else if (httpErrorResponse.error.errors) {
      for (const springError of httpErrorResponse.error.errors) {
        message += springError.defaultMessage;
      }
    } else {
      message = JSON.stringify(httpErrorResponse);
    }
    return {
      errors: [{ componentId: '', errorMessage: message }],
    };
  }

  handleHttpError(httpError: HttpErrorResponse, urid?: string) {
    let errorDetails: ErrorDetail[];
    if (httpError.status === 404) {
      errorDetails = [];
      errorDetails.push(
        new ErrorDetail(
          null,
          'There are no allowed user status action types for this user'
        )
      );
    }

    this.store.dispatch(errors({ errorSummary: { errors: errorDetails } }));
    this.router.navigate([`/user-details/${urid}/status`]);
  }
}
