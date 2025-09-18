import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { catchError, concatMap, map } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';

import { ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';

import { PagedResults } from '@shared/search/util/search-service.util';
import {
  changePageSize,
  loadMessages,
  messagesLoaded,
  navigateToFirstPageOfResults,
  navigateToLastPageOfResults,
  navigateToNextPageOfResults,
  navigateToPageOfResults,
  navigateToPreviousPageOfResults,
  replaySearch,
  searchMessages,
  sortResults,
} from '../actions/itl-messages.actions';
import {
  MessageSearchResult,
  SearchActionPayload,
} from '@kp-administration/itl-messages/model';
import { MessageApiService } from '@kp-administration/itl-messages/service';
import { Store } from '@ngrx/store';
import { selectPageParameters } from '../selectors';

@Injectable()
export class ItlMessagesEffect {
  constructor(
    private messageApiService: MessageApiService,
    private store: Store,
    private actions$: Actions
  ) {}

  searchMessages$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(searchMessages),
      map((action) => loadMessages(action))
    );
  });

  navigateToPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        navigateToFirstPageOfResults,
        navigateToLastPageOfResults,
        navigateToNextPageOfResults,
        navigateToPreviousPageOfResults,
        navigateToPageOfResults
      ),
      map((action) => loadMessages(action))
    );
  });

  changePageSize$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(changePageSize, sortResults),
      map((action) => loadMessages(action))
    );
  });

  replaySearch$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(replaySearch),
      map((action) => loadMessages(action))
    );
  });

  loadMessages$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(loadMessages),
      concatLatestFrom(() => this.store.select(selectPageParameters)),
      concatMap(([action, storedPageParameters]) => {
        const pageParameters = action.loadPageParametersFromState
          ? storedPageParameters
          : action.pageParameters;
        return this.messageApiService
          .search(action.criteria, pageParameters, action.sortParameters)
          .pipe(
            map((pagedResults) =>
              this.mapToAction(pagedResults, { ...action, pageParameters })
            ),
            catchError((httpError: any) =>
              this.handleHttpError(httpError, action)
            )
          );
      })
    );
  });

  private mapToAction(
    pagedResults: PagedResults<MessageSearchResult>,
    actionPayload: SearchActionPayload
  ) {
    const pageParam = actionPayload.pageParameters.page;
    const pageSizeParam = actionPayload.pageParameters.pageSize;
    return messagesLoaded({
      results: pagedResults.items,
      criteria: actionPayload.criteria,
      pagination: {
        currentPage: pagedResults.items.length ? pageParam + 1 : 1,
        pageSize: pageSizeParam ? pageSizeParam : pagedResults.totalResults,
        totalResults: pagedResults.totalResults,
      },
      sortParameters: actionPayload.sortParameters,
    });
  }

  private handleHttpError(
    httpError: HttpErrorResponse,
    action: SearchActionPayload
  ) {
    return action.potentialErrors.has(httpError.status)
      ? [
          errors({
            errorSummary: new ErrorSummary(
              Array.of(action.potentialErrors.get(httpError.status))
            ),
          }),
        ]
      : [
          errors({
            errorSummary: new ErrorSummary(
              Array.of(action.potentialErrors.get('other'))
            ),
          }),
        ];
  }
}
