import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Router } from '@angular/router';
import { catchError, exhaustMap, map, tap } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { ApiErrorHandlingService } from '@shared/services';
import { AccountApiService } from '@registry-web/account-management/service/account-api.service';
import { ClaimAccountActions } from '@claim-account/store/claim-account.actions';
import { CLAIM_ACCOUNT_REQUEST_SUBMITTED } from '@claim-account/claim-account.const';

@Injectable()
export class ClaimAccountEffects {
  submitRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ClaimAccountActions.SUBMIT_REQUEST),
      exhaustMap((action) =>
        this.accountService.claimAccount(action.accountClaimDTO).pipe(
          map((requestId) =>
            ClaimAccountActions.SUBMIT_REQUEST_SUCCESS({ requestId })
          ),
          catchError((error: HttpErrorResponse) => {
            const summary = this.apiErrorHandlingService.transform(error.error);
            return of(errors({ errorSummary: summary }));
          })
        )
      )
    );
  });

  navigateToRequestSubmitted$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(ClaimAccountActions.SUBMIT_REQUEST_SUCCESS),
        tap(() =>
          this.router.navigate(
            [this.router.url + '/' + CLAIM_ACCOUNT_REQUEST_SUBMITTED],
            { skipLocationChange: true }
          )
        )
      ),
    { dispatch: false }
  );

  constructor(
    private actions$: Actions,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private accountService: AccountApiService
  ) {}
}
