import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { RouterNavigatedAction, ROUTER_NAVIGATED } from '@ngrx/router-store';
import { Actions, createEffect, ofType } from '@ngrx/effects';

import { catchError, filter, map, withLatestFrom } from 'rxjs/operators';
import { of } from 'rxjs';

import { GoogleAnalyticsActions } from '@google-analytics/actions';
import { cookiesExist } from '@registry-web/shared/shared.action';
import { RegistrationActionTypes } from '@registry-web/registration/registration.actions';
import { AuthorisedRepresentativesActions } from '@registry-web/account-management/account/authorised-representatives/actions';
import {
  TaskDetailsActions,
  TaskDetailsApiActions,
} from '@registry-web/task-management/task-details/actions';
import { AccountOpeningActionTypes } from '@registry-web/account-opening/account-opening.actions';
import * as fromRegistration from '../../registration/registration.actions';

import { GaFactoryService } from '@google-analytics/services/ga-factory.service';

import { GoogleAnalyticsReducers } from '@google-analytics/reducers';
import { selectGoogleAnalyticsState } from '@google-analytics/selectors/google-analytics.selectors';

import { RequestType } from '@registry-web/task-management/model';
import { TransactionProposalActions } from '@registry-web/transaction-proposal/actions';
import { selectRegistryConfigurationProperty } from '@shared/shared.selector';

@Injectable()
export class GoogleAnalyticsEffects {
  cookiesExist$ = createEffect(() =>
    this.actions$.pipe(
      ofType(cookiesExist),
      withLatestFrom(
        this.store.select(selectRegistryConfigurationProperty, {
          property: 'google.tracking.id',
        })
      ),
      filter(([, googleTrackId]) => googleTrackId),
      map(([, googleTrackId]) => {
        this.ga.initGA(googleTrackId);
        return GoogleAnalyticsActions.loadGoogleAnalyticsSuccess();
      }),
      catchError((error) =>
        of(GoogleAnalyticsActions.loadGoogleAnalyticsFailure({ error }))
      )
    )
  );

  trackPageViews$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(ROUTER_NAVIGATED),
        map((action: RouterNavigatedAction) => {
          const url: string = action.payload.routerState.url;
          if (
            url.indexOf(GoogleAnalyticsReducers.goals.submitDocuments.start) ==
            -1
          ) {
            this.ga.pageViewEmitter(url);
          }
        })
      );
    },
    { dispatch: false }
  );

  userRegistrationStarted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fromRegistration.updateUserRepresentation),
      filter((action) => !!action.userRepresentation.email),
      map((action) => {
        this.ga.eventEmitter({
          eventCategory: 'User registration',
          eventAction: 'User registration started',
        });
        return GoogleAnalyticsActions.updateGoogleAnalyticsMetrics({
          name: 'user registration',
          time: new Date().getTime(),
        });
      })
    );
  });

  userRegistrationCompleted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(RegistrationActionTypes.COMPLETED),
      withLatestFrom(this.store.select(selectGoogleAnalyticsState)),
      map(([action, state]) => {
        const name = state.name;
        const actionStarted = state.time;

        if (name === 'user registration') {
          this.ga.eventEmitter({
            eventCategory: 'User registration',
            eventAction: 'User registration ended',
            eventLabel: 'goal completed',
          });

          this.setGoalDuration(name, actionStarted);
        }

        return GoogleAnalyticsActions.resetGoogleAnalyticsMetrics();
      })
    );
  });

  accountOpeningStarted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountOpeningActionTypes.SET_ACCOUNT_TYPE),
      map((action) => {
        this.ga.eventEmitter({
          eventCategory: 'Account opening request',
          eventAction: 'Account opening started',
        });
        return GoogleAnalyticsActions.updateGoogleAnalyticsMetrics({
          name: 'account opening request',
          time: new Date().getTime(),
        });
      })
    );
  });

  accountOpeningCompleted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountOpeningActionTypes.COMPLETE_REQUEST),
      withLatestFrom(this.store.select(selectGoogleAnalyticsState)),
      map(([action, state]) => {
        const name = state.name;
        const actionStarted = state.time;

        if (name === 'account opening request') {
          this.ga.eventEmitter({
            eventCategory: 'Account opening request',
            eventAction: 'Account opening ended',
            eventLabel: 'goal completed',
          });

          this.setGoalDuration(name, actionStarted);
        }

        return GoogleAnalyticsActions.resetGoogleAnalyticsMetrics();
      })
    );
  });

  addAuthorisedRepresentativeStarted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthorisedRepresentativesActions.setRequestUpdateType),
      map((action) => {
        const actionType = action.updateType;

        if (actionType === 'ADD' || actionType === 'REPLACE') {
          this.ga.eventEmitter({
            eventCategory: 'Add authorised representatives',
            eventAction: 'Add AR started',
          });

          return GoogleAnalyticsActions.updateGoogleAnalyticsMetrics({
            name: 'add authorised representatives',
            time: new Date().getTime(),
          });
        } else {
          return GoogleAnalyticsActions.resetGoogleAnalyticsMetrics();
        }
      })
    );
  });

  addAuthorisedRepresentativeCompleted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthorisedRepresentativesActions.submitUpdateRequestSuccess),
      withLatestFrom(this.store.select(selectGoogleAnalyticsState)),
      map(([action, state]) => {
        const name = state.name;
        const actionStarted = state.time;

        if (name === 'add authorised representatives') {
          this.ga.eventEmitter({
            eventCategory: 'Add authorised representatives',
            eventAction: 'Add AR ended',
            eventLabel: 'goal completed',
          });

          this.setGoalDuration(name, actionStarted);
        }
        return GoogleAnalyticsActions.resetGoogleAnalyticsMetrics();
      })
    );
  });

  submitDocumentsTaskStarted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsActions.loadTaskFromListSuccess),
      filter((action) => !!action?.taskDetails.taskType),
      map((action) => {
        const taskType = action.taskDetails.taskType;

        if (
          taskType === RequestType.AH_REQUESTED_DOCUMENT_UPLOAD ||
          taskType === RequestType.AR_REQUESTED_DOCUMENT_UPLOAD
        ) {
          this.ga.pageViewEmitter('/task-details/submit-documents');
          this.ga.eventEmitter({
            eventCategory: 'Submit documents',
            eventAction: 'Submit documents started',
          });
          return GoogleAnalyticsActions.updateGoogleAnalyticsMetrics({
            name: 'submit documents',
            time: new Date().getTime(),
          });
        } else {
          this.ga.pageViewEmitter(
            `/task-details/${action.taskDetails.requestId}`
          );
          return GoogleAnalyticsActions.resetGoogleAnalyticsMetrics();
        }
      })
    );
  });

  submitDocumentsTaskCompleted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsApiActions.completeTaskWithApprovalSuccess),
      withLatestFrom(this.store.select(selectGoogleAnalyticsState)),
      map(([action, state]) => {
        const taskType = action.taskCompleteResponse.taskDetailsDTO.taskType;
        if (
          taskType === RequestType.AH_REQUESTED_DOCUMENT_UPLOAD ||
          taskType === RequestType.AR_REQUESTED_DOCUMENT_UPLOAD
        ) {
          const name = state.name;
          const actionStarted = state.time;
          this.ga.pageViewEmitter(
            GoogleAnalyticsReducers.goals.submitDocuments.end
          );
          if (name === 'submit documents') {
            this.ga.eventEmitter({
              eventCategory: 'Submit documents',
              eventAction: 'Submit documents ended',
              eventLabel: 'goal completed',
            });

            this.setGoalDuration(name, actionStarted);
          }
        }
        return GoogleAnalyticsActions.resetGoogleAnalyticsMetrics();
      })
    );
  });

  submitTransactionProposalStarted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TransactionProposalActions.fetchLoadAndShowAllowedTransactionTypes
      ),
      map((action) => {
        this.ga.eventEmitter({
          eventCategory: 'Transaction proposal',
          eventAction: 'Transaction proposal started',
        });
        return GoogleAnalyticsActions.updateGoogleAnalyticsMetrics({
          name: 'transaction proposal',
          time: new Date().getTime(),
        });
      })
    );
  });

  submitTransactionProposalCompleted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TransactionProposalActions.submitProposalSuccess,
        TransactionProposalActions.submitReturnExcessAllocationProposalSuccess
      ),
      withLatestFrom(this.store.select(selectGoogleAnalyticsState)),
      map(([action, state]) => {
        const name = state.name;
        const actionStarted = state.time;
        if (name === 'transaction proposal') {
          this.ga.eventEmitter({
            eventCategory: 'Transaction proposal',
            eventAction: 'Transaction proposal ended',
            eventLabel: 'goal completed',
          });

          this.setGoalDuration(name, actionStarted);
        }
        return GoogleAnalyticsActions.resetGoogleAnalyticsMetrics();
      })
    );
  });

  constructor(
    private actions$: Actions,
    private ga: GaFactoryService,
    private store: Store
  ) {}

  private setGoalDuration(categoryName: string, actionStarted: number): void {
    const now = new Date().getTime();
    const duration = now - actionStarted;

    this.ga.setDuration({
      name: 'completed',
      time: duration,
      category: categoryName,
    });
  }
}
