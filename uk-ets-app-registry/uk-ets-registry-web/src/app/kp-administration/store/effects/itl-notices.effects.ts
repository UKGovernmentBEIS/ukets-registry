import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType, concatLatestFrom } from '@ngrx/effects';
import { NoticeApiService } from '@kp-administration/itl-notices/service';
import {
  changeNoticesListPage,
  getNotice,
  getNoticeSuccess,
  loadNotices,
  noticesLoaded,
  selectNoticesPageParameters,
} from '@kp-administration/store';
import { catchError, concatMap, map, switchMap } from 'rxjs/operators';
import { SearchActionPayload } from '@kp-administration/itl-messages/model';
import { PagedResults } from '@shared/search/util/search-service.util';
import { Notice } from '@kp-administration/itl-notices/model';
import { Store } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@registry-web/shared/shared.action';
import { ErrorSummary } from '@registry-web/shared/error-summary';

@Injectable()
export class ItlNoticesEffects {
  constructor(
    private readonly noticeApiService: NoticeApiService,
    private store: Store,
    private readonly actions$: Actions
  ) {}

  loadNotices$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(loadNotices, changeNoticesListPage),
      concatLatestFrom(() => this.store.select(selectNoticesPageParameters)),
      concatMap(([action, storedPageParameters]) => {
        console.log(action.loadPageParametersFromState, storedPageParameters);
        const pageParameters = action.loadPageParametersFromState
          ? storedPageParameters
          : action.pageParameters;
        return this.noticeApiService
          .getNotices(pageParameters, action.sortParameters)
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

  loadNotice$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(getNotice),
      switchMap((payload) =>
        this.noticeApiService.getNoticeByIdentifier(
          payload.noticeIdentifier.toString()
        )
      ),
      map((notice) =>
        getNoticeSuccess({
          notice,
        })
      )
    );
  });

  private mapToAction(
    pagedResults: PagedResults<Notice>,
    actionPayload: SearchActionPayload
  ) {
    const pageParam = actionPayload.pageParameters.page;
    const pageSizeParam = actionPayload.pageParameters.pageSize;
    return noticesLoaded({
      notices: pagedResults.items,
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
