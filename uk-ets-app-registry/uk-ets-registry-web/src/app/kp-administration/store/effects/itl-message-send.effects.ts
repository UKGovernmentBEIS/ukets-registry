import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, mergeMap, tap } from 'rxjs/operators';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiErrorHandlingService } from '@shared/services';

import {
  sendMessage,
  sendMessageSuccess
} from '../actions/itl-message-send.actions';
import { MessageApiService } from '@kp-administration/itl-messages/service';

@Injectable()
export class ItlMessageSendEffects {
  constructor(
    private messageApiService: MessageApiService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private router: Router,
    private store: Store
  ) {}

  sendMessage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(sendMessage),
      mergeMap(action =>
        this.messageApiService.sendMessage(action.content).pipe(
          map(response => {
            if (response.success) {
              return sendMessageSuccess(response);
            } else {
              // return ForgotPasswordActions.validateTokenFailure();
            }
          }),
          catchError((httpErrorResponse: HttpErrorResponse) =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  httpErrorResponse.error
                )
              })
            )
          )
        )
      )
    )
  );

  navigateToSendMessageSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(sendMessageSuccess),
        tap(action => {
          this.router.navigate(
            ['/kpadministration/send-itl-message/send-message-success'],
            {
              skipLocationChange: true
            }
          );
        })
      );
    },
    { dispatch: false }
  );
}
