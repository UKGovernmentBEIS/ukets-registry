import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { map, mergeMap, withLatestFrom } from 'rxjs/operators';
import {
  cancelRequestDocuments,
  cancelRequestDocumentsConfirmed,
  clearRequestDocuments,
  fetchCandidateRecipientsSuccess,
  navigateToAssignUserComment,
  navigateToSelectDocuments,
  navigateToSelectRecipient,
  setComment,
  setRecipient,
  submitDocumentsRequestSuccess,
} from '../actions';
import { navigateTo } from '@shared/shared.action';
import { select, Store } from '@ngrx/store';
import { selectOriginatingPath } from '@request-documents/wizard/reducers/request-document.selector';

@Injectable()
export class RequestDocumentsNavigationEffects {
  constructor(private actions$: Actions, private store: Store) {}

  fetchCandidateRecipientsSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fetchCandidateRecipientsSuccess),
      map(() => {
        return navigateTo({
          route: `/request-documents/select-recipient`,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  selectRecipient$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(setRecipient),
      map(() => {
        return navigateTo({
          route: `/request-documents/check-documents-request`,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  setComment$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(setComment),
      map(() => {
        return navigateTo({
          route: `/request-documents/check-documents-request`,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  cancelRequestDocuments$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(cancelRequestDocuments),
      map((action) =>
        navigateTo({
          route: `/request-documents/cancel-request`,
          extras: {
            queryParams: { goBackRoute: action.route },
            skipLocationChange: true,
          },
        })
      )
    );
  });

  cancelRequestDocumentsConfirmed$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(cancelRequestDocumentsConfirmed),
      withLatestFrom(this.store.pipe(select(selectOriginatingPath))),
      mergeMap(([, originatingPath]) => [
        clearRequestDocuments(),
        navigateTo({
          route: originatingPath,
        }),
      ])
    );
  });

  navigateToSelectDocuments$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateToSelectDocuments),
      map(() =>
        navigateTo({
          route: `/request-documents/select-documents`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  navigateToSelectRecipient$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateToSelectRecipient),
      map(() =>
        navigateTo({
          route: `/request-documents/select-recipient`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  navigateToAssignUserComment$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateToAssignUserComment),
      map(() =>
        navigateTo({
          route: `/request-documents/assigning-user-comment`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  submitDocumentsRequestSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitDocumentsRequestSuccess),
      map(() => {
        return navigateTo({
          route: `/request-documents/request-submitted`,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });
}
