import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { mergeMap, switchMap } from 'rxjs/operators';
import { fetchMessage, loadMessage } from '../actions';
import { MessageApiService } from '@kp-administration/itl-messages/service';

@Injectable()
export class ItlMessageDetailsEffect {
  constructor(
    private messageApiService: MessageApiService,
    private actions$: Actions
  ) {}

  fetchMessage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fetchMessage),
      switchMap((action: { messageId: string }) =>
        this.messageApiService.fetchITLMessage(action.messageId).pipe(
          mergeMap(result => [
            loadMessage({
              messageDetails: result
            })
          ])
        )
      )
    )
  );
}
