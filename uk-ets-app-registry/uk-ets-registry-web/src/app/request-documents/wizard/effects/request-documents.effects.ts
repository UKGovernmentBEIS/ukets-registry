import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { catchError, map, switchMap, withLatestFrom } from 'rxjs/operators';
import { Router } from '@angular/router';
import {
  enterRequestDocumentsWizard,
  fetchCandidateRecipients,
  fetchCandidateRecipientsSuccess,
  setDocumentNames,
  submitDocumentsRequest,
  submitDocumentsRequestSuccess,
} from '../actions';
import { ApiErrorHandlingService } from '@shared/services';
import { select, Store } from '@ngrx/store';
import {
  checkAccountHolderOrigin,
  selectAccountHolderIdentifier,
  selectDocumentsRequest,
} from '@request-documents/wizard/reducers/request-document.selector';
import { errors, navigateTo } from '@shared/shared.action';
import { RequestDocumentsService } from '@request-documents/wizard/services/request-documents.service';
import { ErrorSummary } from '@shared/error-summary';
import { of } from 'rxjs';

@Injectable()
export class RequestDocumentsEffects {
  constructor(
    private actions$: Actions,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private requestDocumentsService: RequestDocumentsService,
    private router: Router,
    private store: Store
  ) {}

  enterRequestDocumentsWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(enterRequestDocumentsWizard),
      map(() =>
        navigateTo({
          route: `/request-documents`,
        })
      )
    );
  });

  selectDocumentNames$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(setDocumentNames),
      withLatestFrom(this.store.pipe(select(checkAccountHolderOrigin))),
      map(([, isAccountHolderOrigin]) => {
        if (isAccountHolderOrigin) {
          return fetchCandidateRecipients();
        } else {
          return navigateTo({
            route: `/request-documents/assigning-user-comment`,
            extras: {
              skipLocationChange: true,
            },
          });
        }
      })
    );
  });

  fetchAndNavigateToSelectRecipient$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fetchCandidateRecipients),
      withLatestFrom(this.store.pipe(select(selectAccountHolderIdentifier))),
      switchMap(([, accountHolderIdentifier]) => {
        return this.requestDocumentsService
          .getAuthorisedRepresentatives(accountHolderIdentifier)
          .pipe(
            map((result) => {
              if (result !== undefined) {
                return fetchCandidateRecipientsSuccess({
                  candidateRecipients: result,
                });
              }
            }),
            catchError((error) =>
              of(
                errors({
                  errorSummary: RequestDocumentsEffects.translateError(error),
                })
              )
            )
          );
      })
    );
  });

  submitDocumentsRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitDocumentsRequest),
      concatLatestFrom(() => this.store.select(selectDocumentsRequest)),
      switchMap(([, documentsRequest]) => {
        return this.requestDocumentsService
          .submitRequest(documentsRequest)
          .pipe(
            map((result) => {
              if (result !== undefined) {
                return submitDocumentsRequestSuccess({
                  submittedRequestIdentifier: result,
                });
              }
            }),
            catchError((error) =>
              of(
                errors({
                  errorSummary: RequestDocumentsEffects.translateError(error),
                })
              )
            )
          );
      })
    );
  });

  private static translateError(error): ErrorSummary {
    return {
      errors: [
        {
          componentId: '',
          errorMessage: error.error
            ? error.error.errorDetails[0].message
            : error,
        },
      ],
    };
  }
}
