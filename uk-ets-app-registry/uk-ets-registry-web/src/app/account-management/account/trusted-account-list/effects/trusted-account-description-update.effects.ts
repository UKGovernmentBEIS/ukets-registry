import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Injectable } from '@angular/core';
import { ApiErrorHandlingService } from '@shared/services';
import { Router } from '@angular/router';
import { select, Store } from '@ngrx/store';
import {
  cancelDescriptionChange,
  clearDescription,
  navigateTo,
  setDescription,
  setDescriptionAction,
  setDescriptionActionAndNavigateToConfirmAction,
  submitChangeDescriptionAction,
  submitChangeDescriptionActionSuccess,
} from '@account-management/account/trusted-account-list/actions/trusted-account-list.actions';
import {
  catchError,
  concatMap,
  map,
  mergeMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { selectAccount } from '@account-management/account/account-details/account.selector';
import { of, pipe } from 'rxjs';
import { TrustedAccountListApiService } from '@trusted-account-list/services';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@shared/shared.action';

@Injectable()
export class TrustedAccountDescriptionUpdateEffects {
  constructor(
    private actions$: Actions,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private trustedAccountListApiService: TrustedAccountListApiService,
    private router: Router,
    private store: Store
  ) {}

  setDescriptionActionAndNavigateToConfirmAction$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(setDescriptionActionAndNavigateToConfirmAction),
      withLatestFrom(this.store.select(pipe(selectAccount))),
      concatMap(([action, account]) => [
        setDescriptionAction({
          descriptionUpdateActionState: action.descriptionUpdateActionState,
        }),
        navigateTo({
          route: `/account/${account.identifier}/trusted-account-list/check-description-answers`,
          extras: { skipLocationChange: true },
        }),
      ])
    );
  });

  submitChangeDescriptionAction$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitChangeDescriptionAction),
      withLatestFrom(this.store.pipe(select(selectAccount))),
      mergeMap(([action, account]) => {
        return this.trustedAccountListApiService
          .submitToUpdateDescriptionTrustedAccount(
            {
              description: action.descriptionUpdateActionState.description,
              accountFullIdentifier:
                action.descriptionUpdateActionState.accountFullIdentifier,
            },
            String(account.identifier)
          )
          .pipe(
            map((result) =>
              submitChangeDescriptionActionSuccess({
                trustedAccountUpdated: result,
              })
            ),
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
    );
  });

  submitChangeDescriptionActionSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitChangeDescriptionActionSuccess),
      withLatestFrom(this.store.pipe(select(selectAccount))),
      mergeMap(([action, account]) => [
        clearDescription(),
        setDescription({
          description: action.trustedAccountUpdated.description,
        }),
        navigateTo({
          route: `/account/${account.identifier}/trusted-account-list/submit-success-change-description`,
          extras: { skipLocationChange: true },
        }),
      ])
    );
  });

  cancelChangeDescription$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(cancelDescriptionChange),
      withLatestFrom(this.store.pipe(select(selectAccount))),
      mergeMap(([, account]) => [
        clearDescription(),
        navigateTo({
          route: `/account/${account.identifier}/trusted-account-list/cancel`,
          extras: { skipLocationChange: true },
        }),
      ])
    );
  });

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );
}
