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
import { canGoBack, errors, navigateTo } from '@shared/shared.action';
import {
  actionLoadData,
  actionLoadDataSuccess,
  actionNavigateToEnterCode,
  actionNavigateToEnterReason,
  actionNavigateToVerification,
  actionSubmitProposal,
  actionSubmitReason,
  actionSubmitToken,
  actionValidateEmailToken,
  actionValidateEmailTokenFailure,
  actionValidateEmailTokenSuccess,
} from '@user-management/token-change/action/token-change.actions';
import { TokenChangeRoutingPaths } from '@user-management/token-change/model/token-change-root-paths.enum';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { AuthApiService } from '../../../auth/auth-api.service';
import { select, Store } from '@ngrx/store';
import { ApiErrorHandlingService } from '@shared/services';
import { TokenChangeService } from '@user-management/token-change/service/token-change.service';
import { selectState } from '@user-management/token-change/reducer/token-change.selectors';
import * as MenuProperties from '@shared/model/navigation-menu';
import { Router } from '@angular/router';
import { MENU_ROUTES } from '@shared/model/navigation-menu';

@Injectable()
export class TokeChangeEffects {
  constructor(
    private authApiService: AuthApiService,
    private tokenChangeService: TokenChangeService,
    private actions$: Actions,
    private store: Store,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private router: Router
  ) {}

  menuProperties = MenuProperties;

  actionLoadData$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actionLoadData),
      concatMap(() =>
        this.tokenChangeService.getTokenDate().pipe(
          map((result) =>
            actionLoadDataSuccess({
              tokenDate: result.newEmail,
            })
          ),
          catchError((error) =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(error),
              })
            )
          )
        )
      )
    );
  });

  actionNavigateToEnterReason$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actionNavigateToEnterReason),
      concatMap(() => {
        return [
          canGoBack({
            goBackRoute: `/user-details/my-profile`,
          }),
          navigateTo({
            route: `/${TokenChangeRoutingPaths.BASE_PATH}`,
          }),
        ];
      })
    );
  });

  actionNavigateToEnterCode$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actionNavigateToEnterCode),
      concatMap(() => {
        return [
          canGoBack({
            goBackRoute: `/${TokenChangeRoutingPaths.BASE_PATH}`,
          }),
          navigateTo({
            route: `/${TokenChangeRoutingPaths.BASE_PATH}/${TokenChangeRoutingPaths.PAGE_2_ENTER_CODE}`,
          }),
        ];
      })
    );
  });

  actionNavigateToVerification$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actionNavigateToVerification),
      concatMap((action) => {
        return this.authApiService.logout(
          location.origin +
            `/${TokenChangeRoutingPaths.BASE_PATH}/${TokenChangeRoutingPaths.PAGE_3_VERIFY}/` +
            action.submittedRequestIdentifier
        );
      })
    );
  });

  actionSubmitReason$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actionSubmitReason),
      switchMap(() => {
        return [actionNavigateToEnterCode()];
      })
    );
  });

  actionSubmitToken$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actionSubmitToken),
      concatMap((action) =>
        this.tokenChangeService.validateOtp(action.otp).pipe(
          concatMap((response) => [actionSubmitProposal()]),
          catchError((error: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error ? error.error : error
                ),
              })
            );
          })
        )
      )
    );
  });

  actionSubmitProposal$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(actionSubmitProposal),
      withLatestFrom(this.store.pipe(select(selectState))),
      switchMap(([, tokenChange]) => {
        return this.tokenChangeService.requestTokenChange(tokenChange).pipe(
          map((result) => {
            if (result !== undefined) {
              return actionNavigateToVerification({
                submittedRequestIdentifier: result,
              });
            }
          }),
          catchError((error) =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                ),
              })
            )
          )
        );
      })
    );
  });

  actionValidateEmailToken$ = createEffect(() =>
    this.actions$.pipe(
      ofType(actionValidateEmailToken),
      mergeMap((action: { token: string }) =>
        this.tokenChangeService.validateToken(action.token).pipe(
          map((result) => {
            if (result) {
              return actionValidateEmailTokenSuccess({
                token: action.token,
              });
            } else {
              return actionValidateEmailTokenFailure();
            }
          }),
          catchError((httpErrorResponse: HttpErrorResponse) =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  httpErrorResponse.error
                ),
              })
            )
          )
        )
      )
    )
  );

  actionValidateEmailTokenSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(actionValidateEmailTokenSuccess),
        tap((action) => {
          this.authApiService.logout(location.origin + MENU_ROUTES.DASHBOARD);
        })
      );
    },
    { dispatch: false }
  );

  actionValidateEmailTokenFailure$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(actionValidateEmailTokenFailure),
        tap((action) => {
          this.router.navigate(
            [
              `/${TokenChangeRoutingPaths.BASE_PATH}/${TokenChangeRoutingPaths.PAGE_4_EXPIRED}`,
            ],
            {
              skipLocationChange: true,
            }
          );
        })
      );
    },
    { dispatch: false }
  );
}
