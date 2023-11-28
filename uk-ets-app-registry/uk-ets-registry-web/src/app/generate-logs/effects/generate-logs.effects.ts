import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  concatMap,
  filter,
  map,
  takeUntil,
  withLatestFrom,
} from 'rxjs/operators';
import { GenerateLogsActions } from '@generate-logs/actions';
import { UILogsApiService } from '@generate-logs/services/ui-logs-api.service';
import { AuthActionTypes } from '@registry-web/auth/auth.actions';
import { Interaction } from '@generate-logs/actions/generate-logs.actions';
import { v4 as uuidv4 } from 'uuid';
import {
  selectCorrelations,
  selectGeneratedLogs,
} from '@generate-logs/selectors/generate-logs.selector';
import { LogsFactoryService } from '@generate-logs/services/logs.factory.service';
import { retryBackoff } from '@shared/utils/rjxs.utils';
import { dateTimeNowFormatted } from '@registry-web/shared/shared.util';

@Injectable()
export class GenerateLogsEffects {
  readonly logsPostThreshold = 80;

  isSignedIn$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActionTypes.IsLoggedInCheckSuccess),
      map(() => {
        const date = dateTimeNowFormatted();
        const interaction: Interaction = {
          label: 'sign in',
          element: 'INPUT',
          id: 'sign-in-button',
          cause: 'login',
          date: date,
          action_identifier: uuidv4(),
        };
        return GenerateLogsActions.receivedUserAction({ interaction });
      })
    )
  );

  // Watch the store and when the logs length exceet the stated threshold
  // post the first half (as for those, asynchronous requests will have probably returned)
  generatedLatestLogObject$ = createEffect(() =>
    this.actions$.pipe(
      ofType(GenerateLogsActions.generatedLatestLogObject),
      withLatestFrom(this.store.select(selectGeneratedLogs)),
      filter(([_, logs]) => logs?.length > this.logsPostThreshold),
      map(([_, logs]) => logs.slice(0, this.logsPostThreshold / 2)),
      map((logs) => GenerateLogsActions.postedLogs({ logs }))
    )
  );

  postedLogs$ = createEffect(() =>
    this.actions$.pipe(
      ofType(GenerateLogsActions.postedLogs),
      map(({ logs }) => logs),
      withLatestFrom(this.store.select(selectCorrelations)),
      map(([logs, correlations]) => {
        //Here we POST-PROCESS the outgoing logs and make sure they have the correct
        //interaction identifier correlation before being posted to the server
        return this.logFactoryService.updateInteractionLogs(logs, correlations);
      }),
      concatMap((logs) =>
        this.logApiService.postLogs(logs).pipe(
          map(() => GenerateLogsActions.postedLogsAccepted({ logs })),
          retryBackoff({
            maxRetries: 5,
            initialDelay: 1000,
            onFailure: () => {
              console.error(
                'Unable to establish connectivity to ui-logs-api service. Terminating submission of logs.'
              );
              this.store.dispatch(GenerateLogsActions.postedLogsFailed());
            },
          })
        )
      ),
      //cancel the entire effect, if this is triggered it means that the service most probably
      //is not reachable
      takeUntil(
        this.actions$.pipe(ofType(GenerateLogsActions.postedLogsFailed))
      )
    )
  );

  //When an error is triggered post all logs
  apiErrorRequestFailure$ = createEffect(() =>
    this.actions$.pipe(
      ofType(GenerateLogsActions.apiRequestFailure),
      withLatestFrom(this.store.select(selectGeneratedLogs)),
      map(([_, logs]) => GenerateLogsActions.postedLogs({ logs }))
    )
  );

  constructor(
    private actions$: Actions,
    private store: Store,
    private logFactoryService: LogsFactoryService,
    private logApiService: UILogsApiService
  ) {}
}
