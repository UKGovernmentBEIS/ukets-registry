import { Injectable } from '@angular/core';
import { Actions, concatLatestFrom, createEffect, ofType } from '@ngrx/effects';
import {
  TaskDetailsActions,
  TaskDetailsApiActions,
  TaskDetailsNavigationActions,
} from '@task-details/actions';
import {
  catchError,
  concatMap,
  exhaustMap,
  map,
  mergeMap,
  switchMap,
  withLatestFrom,
} from 'rxjs/operators';
import { SubmitDocumentService } from '@task-management/service';
import { of } from 'rxjs';
import { Router } from '@angular/router';
import { ErrorDetail } from '@shared/error-summary';
import { canGoBack, errors } from '@registry-web/shared/shared.action';
import {
  ActionError,
  apiErrorToBusinessError,
  REQUEST_TYPE_VALUES,
  RequestedDocumentUploadTaskDetails,
  TaskActionErrorResponse,
  TaskDetails,
  TaskFileDownloadInfo,
  TaskOutcome,
} from '@task-management/model';
import { ExportFileService } from '@shared/export-file/export-file.service';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpEventType,
} from '@angular/common/http';
import { select, Store } from '@ngrx/store';
import { requestUploadSelectedFileForDocumentRequest } from '@shared/file/actions/file-upload-form.actions';
import {
  processSelectedFileError,
  uploadSelectedFileError,
  uploadSelectedFileForDocumentRequest,
  uploadSelectedFileHasStarted,
  uploadSelectedFileInProgress,
} from '@shared/file/actions/file-upload-api.actions';
import { UploadStatus } from '@shared/model/file';
import {
  selectTask,
  selectUploadedFileDetails,
  selectUserComment,
  selectUserDecisionForTask,
} from '@task-details/reducers/task-details.selector';
import { ApiErrorHandlingService } from '@shared/services';
import { UserProfileService } from '@user-management/service';
import {
  fetchAccount,
  populateAccount,
  updateTaskSuccess,
} from '@task-details/actions/task-details.actions';
import { AccountHolderService } from '@account-opening/account-holder/account-holder.service';
import { AccountApiService } from '@account-management/service/account-api.service';
import { TaskService } from '@shared/services/task-service';

@Injectable()
export class TaskDetailsEffects {
  constructor(
    private taskService: TaskService,
    private exportFileService: ExportFileService,
    private actions$: Actions,
    private router: Router,
    private store: Store,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private submitDocumentService: SubmitDocumentService,
    private userProfileService: UserProfileService,
    private accountHolderService: AccountHolderService,
    private accountApiService: AccountApiService
  ) {}

  prepareNavigationToTask$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsActions.prepareNavigationToTask),
      concatMap((action: { taskId: string }) => {
        return [
          TaskDetailsActions.fetchTask(action),
          canGoBack({ goBackRoute: null }),
        ];
      })
    );
  });

  fetchTask$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsActions.fetchTask),
      switchMap((action: { taskId: string }) =>
        this.taskService.fetchOneTask(action.taskId).pipe(
          map((result) => TaskDetailsActions.loadTask({ taskDetails: result })),
          catchError((httpError) => this.handleHttpError(httpError))
        )
      )
    )
  );

  fetchTaskFromList$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsActions.loadTaskFromList),
      switchMap((action: { taskId: string }) =>
        this.taskService.fetchOneTask(action.taskId).pipe(
          map((result) =>
            TaskDetailsActions.loadTaskFromListSuccess({ taskDetails: result })
          ),
          catchError((httpError) => this.handleHttpError(httpError))
        )
      )
    )
  );

  fetchTaskHistory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsActions.fetchTaskHistory),
      mergeMap((action: { requestId: string }) =>
        this.taskService.taskHistory(action.requestId).pipe(
          map((results) =>
            TaskDetailsActions.fetchTaskHistorySuccess({ results })
          ),
          catchError((httpError) => this.handleHttpError(httpError))
        )
      )
    )
  );

  taskHistoryAddComment$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsActions.taskHistoryAddComment),
      mergeMap((action: { requestId: string; comment: string }) =>
        this.taskService.addComment(action.requestId, action.comment).pipe(
          map((result) =>
            TaskDetailsActions.taskHistoryAddCommentSuccess({
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
      ofType(TaskDetailsActions.taskHistoryAddCommentSuccess),
      mergeMap((action: { requestId: string }) => {
        return of(
          TaskDetailsActions.fetchTaskHistory({ requestId: action.requestId })
        );
      }),
      catchError((err) => of(TaskDetailsActions.fetchTaskHistoryError(err)))
    );
  });

  updateTask$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsActions.updateTask),
      mergeMap((action) =>
        this.taskService.update(action).pipe(
          map((result: TaskDetails) => updateTaskSuccess({ result: result })),
          catchError((httpError) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  httpError.error
                ),
              })
            );
          })
        )
      )
    )
  );

  fetchAccount$ = createEffect(() =>
    this.actions$.pipe(
      ofType(fetchAccount),
      mergeMap(({ accountId: accountId }) => {
        return this.accountApiService.fetchAccount(String(accountId)).pipe(
          map((result) => {
            return populateAccount({ account: result });
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
    )
  );

  /**
   * when setting the comments in the complete screen then
   * depending on the taskoutcome proceed with
   * 1.When users decide to reject task, then d
   */
  setCommentAndPressedComplete$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsActions.setCompleteTask),
      withLatestFrom(
        this.store.pipe(select(selectTask)),
        this.store.pipe(select(selectUserDecisionForTask))
      ),
      map(([action, taskFromStore, userDecisionFromStore]) => {
        const taskOutcome: TaskOutcome = userDecisionFromStore;
        const taskDetails: TaskDetails = taskFromStore;
        if (taskOutcome === TaskOutcome.REJECTED) {
          return TaskDetailsApiActions.completeTaskWithRejection({
            comment: action.completeTaskFormInfo.comment,
            taskId: taskDetails.requestId,
          });
        } else {
          if (
            REQUEST_TYPE_VALUES[taskDetails.taskType]
              .requiresOtpVerificationOnApproval
          ) {
            return TaskDetailsApiActions.otpVerificationForTaskRequest({
              otp: action.completeTaskFormInfo.otp,
            });
          } else {
            return TaskDetailsApiActions.completeTaskWithApproval({
              comment: action.completeTaskFormInfo.comment,
              taskId: taskDetails.requestId,
            });
          }
        }
      })
    )
  );

  rejectTask$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsApiActions.completeTaskWithRejection),
      exhaustMap((action) =>
        this.taskService
          .complete(action.taskId, action.comment, TaskOutcome.REJECTED)
          .pipe(
            map((taskCompleteResponse) =>
              TaskDetailsApiActions.completeTaskWithRejectionSuccess({
                taskCompleteResponse,
              })
            ),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          )
      )
    )
  );

  completeOnlyTaskApprove$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsActions.approveTaskDecisionForCompleteOnlyTask),
      withLatestFrom(
        this.store.pipe(select(selectTask)),
        this.store.pipe(select(selectUserComment))
      ),
      map(([, taskDetailsFromStore, userComment]) => {
        const taskDetails: TaskDetails = taskDetailsFromStore;
        return TaskDetailsApiActions.completeTaskWithApproval({
          taskId: taskDetails.requestId,
          comment: userComment,
        });
      })
    )
  );

  approveTask$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsApiActions.completeTaskWithApproval),
      exhaustMap((action) =>
        this.taskService
          .complete(action.taskId, action.comment, TaskOutcome.APPROVED)
          .pipe(
            map((taskCompleteResponse) =>
              TaskDetailsApiActions.completeTaskWithApprovalSuccess({
                taskCompleteResponse,
              })
            ),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          )
      )
    )
  );

  approvalSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsApiActions.completeTaskWithApprovalSuccess),
      map((action) => {
        const taskDetails: TaskDetails =
          action.taskCompleteResponse.taskDetailsDTO;
        if (
          REQUEST_TYPE_VALUES[taskDetails.taskType]
            .goToConfirmationPageAfterCompletion
        ) {
          return TaskDetailsNavigationActions.navigateToTaskApproved();
        } else {
          return TaskDetailsNavigationActions.navigateToTaskDetails({
            extras: { skipLocationChange: true },
          });
        }
      })
    )
  );

  /**
   *
   */
  otpVerification$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsApiActions.otpVerificationForTaskRequest),
      exhaustMap((action) =>
        this.userProfileService.validateOtp(action.otp).pipe(
          map(() => TaskDetailsApiActions.optVerificationForTaskSuccess()),
          catchError((error: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                ),
              })
            );
          })
        )
      )
    )
  );

  otpVerificationSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsApiActions.optVerificationForTaskSuccess),
      withLatestFrom(
        this.store.pipe(select(selectTask)),
        this.store.pipe(select(selectUserComment))
      ),
      map(([, taskFromStore, userCommentFromStore]) => {
        const taskDetails: TaskDetails = taskFromStore;
        return TaskDetailsApiActions.completeTaskWithApproval({
          comment: userCommentFromStore,
          taskId: taskDetails.requestId,
        });
      })
    )
  );

  fetchTaskRelatedFile$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TaskDetailsActions.fetchTaskRelatedFile),
        mergeMap((action: { fileId: number }) => {
          return this.taskService.fetchTaskRelatedFile(action.fileId).pipe(
            map((result) => {
              this.exportFileService.export(
                result.body,
                this.exportFileService.getContentDispositionFilename(
                  result.headers.get('Content-Disposition')
                )
              );
            }),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          );
        })
      );
    },
    { dispatch: false }
  );

  fetchTaskFile$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(
          TaskDetailsActions.fetchTaskUserFile,
          TaskDetailsActions.fetchAccountOpeningSummaryFile
        ),
        mergeMap((action: { taskFileDownloadInfo: TaskFileDownloadInfo }) => {
          return this.taskService
            .fetchRequestedFile(action.taskFileDownloadInfo)
            .pipe(
              map((result) => {
                this.exportFileService.export(
                  result.body,
                  this.exportFileService.getContentDispositionFilename(
                    result.headers.get('Content-Disposition')
                  )
                );
              }),
              catchError((error: HttpErrorResponse) =>
                of(
                  errors({
                    errorSummary: this.apiErrorHandlingService.transform(
                      error.error
                    ),
                  })
                )
              )
            );
        })
      );
    },
    { dispatch: false }
  );

  startSubmitDocument$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(requestUploadSelectedFileForDocumentRequest),
      map((action) => {
        return uploadSelectedFileForDocumentRequest({
          file: action.file,
          documentName: action.documentName,
          fileUploadIndex: action.fileUploadIndex,
          fileId: action.fileId,
        });
      })
    );
  });
  // TODO: these should be moved to another module(file upload module?)
  //  to support upload from other features as well.
  uploadSelectedDocument$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(uploadSelectedFileForDocumentRequest),
      concatLatestFrom(() => [
        this.store.select(selectTask),
        this.store.select(selectUploadedFileDetails),
      ]),
      switchMap(([action, task, uploadedFiles]) =>
        this.submitDocumentService
          .uploadSelectedDocument(
            action.file,
            action.documentName,
            task,
            action.fileId,
            uploadedFiles.totalFileUploads
          )
          .pipe(
            mergeMap((event: HttpEvent<any>) => {
              switch (event.type) {
                case HttpEventType.Sent:
                  return of(
                    uploadSelectedFileHasStarted({
                      status: UploadStatus.Started,
                    })
                  );
                case HttpEventType.UploadProgress:
                  return of(
                    uploadSelectedFileInProgress({
                      progress: Math.round((100 * event.loaded) / event.total),
                    })
                  );
                case HttpEventType.Response:
                  return of(
                    TaskDetailsActions.uploadSelectedFileSuccess({
                      fileId: event.body,
                      fileUploadIndex: action.fileUploadIndex,
                    })
                  );
                default:
                  return of(
                    uploadSelectedFileError({
                      status: UploadStatus.Failed,
                    })
                  );
              }
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                processSelectedFileError({
                  status: UploadStatus.Failed,
                }),
                errors({
                  errorSummary: error.error
                    ? this.apiErrorHandlingService.transform(error.error)
                    : this.apiErrorHandlingService.buildUiError(
                        JSON.stringify(error)
                      ),
                })
              );
            })
          )
      )
    );
  });

  deleteSelectedDocument$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsActions.deleteSelectedFile),
      concatLatestFrom(() => [
        this.store.select(selectTask),
        this.store.select(selectUploadedFileDetails),
      ]),
      switchMap(([action, task, uploadedFiles]) =>
        this.submitDocumentService
          .deleteSelectedDocument(
            action.fileId,
            task.requestId,
            uploadedFiles.totalFileUploads,
            (task as RequestedDocumentUploadTaskDetails).userUrid,
            (task as RequestedDocumentUploadTaskDetails).accountHolderIdentifier
          )
          .pipe(
            map(() => {
              return TaskDetailsActions.deleteSelectedFileSuccess({
                fileId: action.fileId,
              });
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: error.error
                    ? this.apiErrorHandlingService.transform(error.error)
                    : this.apiErrorHandlingService.buildUiError(
                        JSON.stringify(error)
                      ),
                })
              );
            })
          )
      )
    );
  });

  reloadTaskDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TaskDetailsActions.uploadSelectedFileSuccess,
        TaskDetailsActions.deleteSelectedFileSuccess
      ),
      concatLatestFrom(() => this.store.select(selectTask)),
      map(([, task]) =>
        TaskDetailsActions.loadTaskFromList({ taskId: task.requestId })
      )
    );
  });

  // TODO: since the error messages are build in the server we do not nead to recreate them here

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

  /**
   * TODO: error messages will by default send from the backend
   * TODO: Delete code mappings as new rules are implemented in the backend
   */
  buildErrorDetail(actionError: ActionError): ErrorDetail {
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
