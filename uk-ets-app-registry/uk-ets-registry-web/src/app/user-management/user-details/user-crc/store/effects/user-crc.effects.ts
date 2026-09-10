import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { ApiErrorHandlingService } from '@shared/services';
import { Store } from '@ngrx/store';
import { catchError, concatMap, exhaustMap } from 'rxjs/operators';
import { UserCrcActions } from '@user-crc/store/actions';
import { selectUserURID } from '@user-crc/store/selectors';
import { UserCrcService } from '@user-crc/service';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@shared/shared.action';
import { of } from 'rxjs';
import { concatLatestFrom } from '@ngrx/operators';

@Injectable()
export class UserCrcEffects {
  constructor(
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store,
    private userCrcService: UserCrcService
  ) {}

  submitUserCrcUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserCrcActions.requestUserCrcUpdate),
      concatLatestFrom(() => this.store.select(selectUserURID)),
      exhaustMap(([action, urid]) => {
        return this.userCrcService.submitRequest(urid, action.request).pipe(
          concatMap(() => {
            return [UserCrcActions.requestUserCrcUpdateSuccess()];
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
