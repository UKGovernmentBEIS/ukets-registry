import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { concatLatestFrom } from '@ngrx/operators';
import { of } from 'rxjs';
import { catchError, exhaustMap, map, mergeMap } from 'rxjs/operators';
import { ApiErrorHandlingService } from '@shared/services';
import { ExportFileService } from '@shared/export-file/export-file.service';
import {
  ActionError,
  apiErrorToBusinessError,
  TaskActionErrorResponse,
  RegulatorNoticeTaskCompleteResponse,
  TaskOutcome,
  TaskFileDownloadInfo,
} from '@shared/task-and-regulator-notice-management/model';
import { RegulatorNoticeDetailsActions } from '@regulator-notice-management/details/store/regulator-notice-details.actions';
import { selectNoticeDetails } from '@regulator-notice-management/details/store/regulator-notice-details.selectors';
import { errors } from '@registry-web/shared/shared.action';
import { TaskService } from '@registry-web/shared/services/task-service';
import { ErrorDetail } from '@registry-web/shared/error-summary';

@Injectable()
export class RegulatorNoticeDetailsEffects {
  completeNotice$ = createEffect(() =>
    this.actions$.pipe(
      ofType(RegulatorNoticeDetailsActions.COMPLETE),
      concatLatestFrom(() => this.store.select(selectNoticeDetails)),
      exhaustMap(([, noticeDetails]) =>
        this.taskService
          .complete({
            taskId: noticeDetails.requestId,
            comment: null,
            taskOutcome: TaskOutcome.APPROVED,
          })
          .pipe(
            map((taskCompleteResponse) => {
              const noticeResponse =
                taskCompleteResponse as RegulatorNoticeTaskCompleteResponse;
              return RegulatorNoticeDetailsActions.COMPLETE_SUCCESS({
                completedNoticeDetails: noticeResponse.taskDetailsDTO,
              });
            }),
            catchError((res: HttpErrorResponse) => {
              const summary = this.apiErrorHandlingService.transform(res.error);
              return of(errors({ errorSummary: summary }));
            })
          )
      )
    )
  );

  fetchTaskHistory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(RegulatorNoticeDetailsActions.FETCH_HISTORY),
      mergeMap((action: { requestId: string }) =>
        this.taskService.taskHistory(action.requestId).pipe(
          map((results) =>
            RegulatorNoticeDetailsActions.FETCH_HISTORY_SUCCESS({ results })
          ),
          catchError((httpError) => this.handleHttpError(httpError))
        )
      )
    )
  );

  taskHistoryAddComment$ = createEffect(() =>
    this.actions$.pipe(
      ofType(RegulatorNoticeDetailsActions.ADD_HISTORY_COMMENT),
      mergeMap((action: { requestId: string; comment: string }) =>
        this.taskService.addComment(action.requestId, action.comment).pipe(
          map((result) =>
            RegulatorNoticeDetailsActions.ADD_HISTORY_COMMENT_SUCCESS({
              requestId: result.requestId,
            })
          ),
          catchError((httpError) => this.handleHttpError(httpError))
        )
      )
    )
  );

  taskHistoryAddCommentSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(RegulatorNoticeDetailsActions.ADD_HISTORY_COMMENT_SUCCESS),
      map(({ requestId }) =>
        RegulatorNoticeDetailsActions.FETCH_HISTORY({ requestId })
      )
    );
  });

  fetchFile$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(RegulatorNoticeDetailsActions.FETCH_FILE),
        mergeMap((action: { info: TaskFileDownloadInfo }) => {
          return this.taskService.fetchRequestedFile(action.info).pipe(
            map((result) => {
              this.exportFileService.export(
                result.body,
                this.exportFileService.getContentDispositionFilename(
                  result.headers.get('Content-Disposition')
                )
              );
            }),
            catchError((error: HttpErrorResponse) => {
              const errorSummary = this.apiErrorHandlingService.transform(
                error.error
              );
              return of(errors({ errorSummary }));
            })
          );
        })
      );
    },
    { dispatch: false }
  );

  constructor(
    private actions$: Actions,
    private store: Store,
    private taskService: TaskService,
    private exportFileService: ExportFileService,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}

  private handleHttpError(httpError: HttpErrorResponse) {
    const errorDetails: ErrorDetail[] = [];
    const errorResponse: TaskActionErrorResponse = apiErrorToBusinessError(
      httpError.error
    );
    const errorMsg: string = errorResponse.message;
    if (errorMsg) {
      errorDetails.push({ componentId: null, errorMessage: errorMsg });
    }
    if (errorResponse.errors) {
      errorResponse.errors.forEach((actionError) =>
        errorDetails.push(this.buildErrorDetail(actionError))
      );
    } else {
      errorDetails.push({
        componentId: null,
        errorMessage: 'Unexpected error',
      });
    }
    return of(errors({ errorSummary: { errors: errorDetails } }));
  }

  private buildErrorDetail(actionError: ActionError): ErrorDetail {
    let message: string;
    switch (actionError.code) {
      case 'COMMENT_REQUIRED':
        message = 'Comment is required';
        break;
      case 'USER_SHOULD_BE_THE_CLAIMANT':
        message = 'You cannot comment on task that you have not claimed';
        break;
      case 'NO_PERMISSION_TO_COMPLETE_TASK':
        message = actionError.message;
        break;
      case 'INITIATOR_NOT_ALLOWED_TO_COMPLETE_TASK':
        message = actionError.message;
        break;
      case 'AR_NOT_ALLOWED_TO_COMPLETE_TASK_INITIATED_BY_ADMIN':
        message = actionError.message;
        break;
      case 'NO_PERMISSION_TO_READ_TASK':
        message = actionError.message;
        break;
      default:
        message = actionError.message;
    }
    return { componentId: null, errorMessage: message };
  }
}
