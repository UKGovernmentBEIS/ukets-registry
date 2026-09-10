import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { ApiErrorHandlingService } from '@shared/services';
import { Store } from '@ngrx/store';
import { catchError, concatMap, exhaustMap } from 'rxjs/operators';
import { UserAgentActions } from '@user-agent/store/actions';
import { selectUserURID } from '@user-agent/store/selectors';
import { UserAgentService } from '@user-agent/service';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@shared/shared.action';
import { of } from 'rxjs';
import { concatLatestFrom } from '@ngrx/operators';

@Injectable()
export class UserAgentEffects {
  constructor(
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store,
    private userAgentService: UserAgentService
  ) {}

  submitUserAgentUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserAgentActions.requestUserAgentUpdate),
      concatLatestFrom(() => this.store.select(selectUserURID)),
      exhaustMap(([action, urid]) => {
        return this.userAgentService.submitRequest(urid, action.request).pipe(
          concatMap(() => {
            return [UserAgentActions.requestUserAgentUpdateSuccess()];
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
    );
  });
}
