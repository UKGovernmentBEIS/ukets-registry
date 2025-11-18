import { Injectable } from '@angular/core';
import { act, Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
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
import { Observable, of, throwError } from 'rxjs';
import { ErrorDetail } from '@shared/error-summary';
import { canGoBack, errors } from '@registry-web/shared/shared.action';
import {
  AccountHolderChangeTaskDetails,
  ActionError,
  apiErrorToBusinessError,
  REQUEST_TYPE_VALUES,
  RequestedDocumentUploadTaskDetails,
  RequestType,
  TaskActionErrorResponse,
  TaskCompleteResponse,
  TaskDetails,
  TaskFileDownloadInfo,
  TaskOutcome,
  TaskUpdateAction,
  TransactionTaskDetailsBase,
} from '@task-management/model';
import { ExportFileService } from '@shared/export-file/export-file.service';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpEventType,
} from '@angular/common/http';
import { Store } from '@ngrx/store';
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
  selectAmountPaid,
  selectPaymentMethod,
  selectPaymentUUID,
  selectTask,
  selectTaskDeadlineAsDate,
  selectUploadedFileDetails,
  selectUserComment,
  selectUserDecisionForTask,
} from '@task-details/reducers/task-details.selector';
import { ApiErrorHandlingService } from '@shared/services';
import { UserProfileService } from '@user-management/service';
import {
  bacsPaymentCancelledSuccess,
  bacsPaymentCompleteSuccess,
  downloadPaymentReceipt,
  fetchAccount,
  populateAccount,
  submitChangedTaskDeadlineSuccess,
  submitMakePaymentSuccess,
  updateTaskSuccess,
} from '@task-details/actions/task-details.actions';
import { AccountApiService } from '@account-management/service/account-api.service';
import { TaskService } from '@shared/services/task-service';
import {
  navigateToBACSAwaitingPayment,
  navigateToBACSDetailsPaymentMethod,
  navigateToChangeTaskDeadlineSuccess,
  navigateToCheckChangeTaskDeadline,
  navigateToGovUKPayService,
} from '../actions/task-details-navigation.actions';
import { TransactionType } from '@registry-web/shared/model/transaction';
import { RequestPaymentService } from '@request-payment/services';
import { AccountHolderChangeService } from '@registry-web/account-management/account/change-account-holder-wizard/service';

@Injectable()
export class TaskDetailsEffects {
  constructor(
    private taskService: TaskService,
    private exportFileService: ExportFileService,
    private actions$: Actions,
    private store: Store,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private submitDocumentService: SubmitDocumentService,
    private userProfileService: UserProfileService,
    private accountApiService: AccountApiService,
    private requestPaymentService: RequestPaymentService,
    private accountHolderChangeService: AccountHolderChangeService
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

  /**
   * Makes an api call to account and adds the operator in the transferring account
   * object in task details. For Excess Allocation tasks only.
   *
   * @param task: TaskDetails
   * @returns Observable<TaskDetails>
   */
  addOperatorToTask(task: TaskDetails): Observable<TaskDetails> {
    const trTaskDetails = task as TransactionTaskDetailsBase;
    if (
      trTaskDetails.trType === TransactionType.ExcessAllocation &&
      trTaskDetails?.transferringAccount?.identifier
    ) {
      return this.accountApiService
        .fetchAccount(String(trTaskDetails?.transferringAccount?.identifier))
        .pipe(
          map(
            (account) =>
              ({
                ...task,
                transferringAccount: {
                  ...(task as TransactionTaskDetailsBase).transferringAccount,
                  operator: { ...account.operator },
                },
              }) as TaskDetails
          ),
          catchError((error) => throwError(() => new Error(error)))
        );
    } else {
      return of(task);
    }
  }

  addOperatorToTaskCompleteResponse(
    taskCompleteResponse: TaskCompleteResponse
  ): Observable<TaskCompleteResponse> {
    return this.addOperatorToTask(taskCompleteResponse.taskDetailsDTO).pipe(
      map((taskDetailsDTO) => ({
        ...taskCompleteResponse,
        taskDetailsDTO,
      }))
    );
  }

  /**
   * Conditionally fetches additional task data.
   *
   * @param task: TaskDetails
   * @returns Observable<TaskDetails>
   */
  private fetchAdditionalTaskDetailsData(
    task: TaskDetails
  ): Observable<TaskDetails> {
    if (task.taskType === RequestType.ACCOUNT_HOLDER_CHANGE) {
      const taskDetails = task as AccountHolderChangeTaskDetails;
      return this.accountHolderChangeService
        .getAccountHolderOrphan(
          taskDetails.currentAccountHolder.id,
          taskDetails.accountNumber
        )
        .pipe(
          map((isAccountHolderOrphan) => ({
            ...taskDetails,
            isAccountHolderOrphan,
          }))
        );
    }

    return of(task);
  }

  fetchTask$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsActions.fetchTask),
      switchMap((action: { taskId: string }) =>
        this.taskService.fetchOneTask(action.taskId).pipe(
          mergeMap((task) => this.addOperatorToTask(task)),
          mergeMap((task) => this.fetchAdditionalTaskDetailsData(task)),
          map((result: TaskDetails) =>
            TaskDetailsActions.loadTask({ taskDetails: result })
          ),
          catchError((httpError) => this.handleHttpError(httpError))
        )
      )
    );
  });

  fetchTaskFromList$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsActions.loadTaskFromList),
      switchMap((action: { taskId: string }) =>
        this.taskService.fetchOneTask(action.taskId).pipe(
          mergeMap((task) => this.addOperatorToTask(task)),
          mergeMap((task) => this.fetchAdditionalTaskDetailsData(task)),
          map((result) =>
            TaskDetailsActions.loadTaskFromListSuccess({ taskDetails: result })
          ),
          catchError((httpError) => this.handleHttpError(httpError))
        )
      )
    );
  });

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
      concatLatestFrom(() => [
        this.store.select(selectTask),
        this.store.select(selectUserDecisionForTask),
      ]),
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
              amountPaid: action.completeTaskFormInfo.amountPaid,
              taskId: taskDetails.requestId,
            });
          }
        }
      })
    )
  );

  rejectTask$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsApiActions.completeTaskWithRejection),
      exhaustMap((action) =>
        this.taskService
          .complete({
            taskId: action.taskId,
            comment: action.comment,
            taskOutcome: TaskOutcome.REJECTED,
          })
          .pipe(
            mergeMap((taskCompleteResponse) =>
              this.addOperatorToTaskCompleteResponse(taskCompleteResponse)
            ),
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
    );
  });

  completeOnlyTaskApprove$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsActions.approveTaskDecisionForCompleteOnlyTask),
      withLatestFrom(
        this.store.select(selectTask),
        this.store.select(selectUserComment)
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

  approveTask$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsApiActions.completeTaskWithApproval),
      exhaustMap((action) =>
        this.taskService
          .complete({
            taskId: action.taskId,
            comment: action.comment,
            amountPaid: action.amountPaid,
            taskOutcome: TaskOutcome.APPROVED,
          })
          .pipe(
            mergeMap((taskCompleteResponse) =>
              this.addOperatorToTaskCompleteResponse(taskCompleteResponse)
            ),
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
    );
  });

  fetchPaymentCompleteResponseWithExternalService$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TaskDetailsApiActions.fetchPaymentCompleteResponseWithExternalService
      ),
      exhaustMap((action) =>
        this.requestPaymentService
          .getMakePaymentResponse(action.requestId)
          .pipe(
            map((taskCompleteResponse) =>
              TaskDetailsApiActions.fetchPaymentCompleteResponseWithExternalServiceSuccess(
                {
                  taskCompleteResponse,
                }
              )
            ),
            catchError((httpErrorResponse: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform({
                    errorDetails: [
                      {
                        message: httpErrorResponse.error,
                      },
                    ],
                  }),
                })
              );
            })
          )
      )
    );
  });

  fetchPaymentViaWebLinkCompleteResponseWithExternalService$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(
          TaskDetailsApiActions.fetchPaymentViaWebLinkCompleteResponseWithExternalService
        ),
        exhaustMap((action) =>
          this.requestPaymentService
            .getMakePaymentWebLinkResponse(action.uuid)
            .pipe(
              map((taskCompleteResponse) =>
                TaskDetailsApiActions.fetchPaymentViaWebLinkCompleteResponseWithExternalServiceSuccess(
                  {
                    taskCompleteResponse,
                  }
                )
              ),
              catchError((httpErrorResponse: HttpErrorResponse) => {
                return of(
                  errors({
                    errorSummary: this.apiErrorHandlingService.transform({
                      errorDetails: [
                        {
                          message: httpErrorResponse.error,
                        },
                      ],
                    }),
                  })
                );
              })
            )
        )
      );
    }
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
      concatLatestFrom(() => [
        this.store.select(selectTask),
        this.store.select(selectUserComment),
        this.store.select(selectAmountPaid),
      ]),
      map(([, taskFromStore, userCommentFromStore, amountPaid]) => {
        const taskDetails: TaskDetails = taskFromStore;
        return TaskDetailsApiActions.completeTaskWithApproval({
          comment: userCommentFromStore,
          amountPaid: amountPaid,
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
              const errorSummary = error.error
                ? this.apiErrorHandlingService.transform(error.error)
                : this.apiErrorHandlingService.buildUiError(
                    JSON.stringify(error)
                  );
              return of(
                TaskDetailsActions.uploadSelectedFileError({
                  fileUploadIndex: action.fileUploadIndex,
                  errorMessage: errorSummary.errors[0].errorMessage,
                }),
                processSelectedFileError({
                  status: UploadStatus.Failed,
                }),
                errors({ errorSummary })
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

  updateTaskDeadline$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsActions.updateTaskDeadline),
      map(() => navigateToCheckChangeTaskDeadline())
    );
  });

  submitChangedTaskDeadline$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsActions.submitChangedTaskDeadline),
      concatLatestFrom(() => [
        this.store.select(selectTaskDeadlineAsDate),
        this.store.select(selectTask),
      ]),
      mergeMap(([action, deadline, task]) =>
        this.taskService
          .update({
            taskDetails: { ...task, deadline },
            updateInfo: deadline.toISOString(),
            taskUpdateAction: TaskUpdateAction.UPDATE_DEADLINE,
          })
          .pipe(
            map((result: TaskDetails) =>
              submitChangedTaskDeadlineSuccess({ result: result })
            ),
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
    );
  });

  submitChangedTaskDeadlineSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsActions.submitChangedTaskDeadlineSuccess),
      map(() => navigateToChangeTaskDeadlineSuccess())
    );
  });

  bacsPaymentCompleteOrCancel$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsActions.bacsPaymentCompleteOrCancelled),
      concatLatestFrom(() => [this.store.select(selectPaymentUUID)]),
      mergeMap(([action, uuid]) =>
        this.requestPaymentService
          .bacsPaymentCompleteOrCancel({ uuid, status: action.status })
          .pipe(
            map(() => {
              if (action.status === 'SUBMITTED') {
                return bacsPaymentCompleteSuccess();
              } else if (action.status === 'CANCELLED') {
                return bacsPaymentCancelledSuccess();
              }
            }),
            catchError((httpErrorResponse: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform({
                    errorDetails: [
                      {
                        message: httpErrorResponse.error,
                      },
                    ],
                  }),
                })
              );
            })
          )
      )
    );
  });

  submitBacsPaymentCompleteSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsActions.bacsPaymentCompleteSuccess),
      map(() => navigateToBACSAwaitingPayment())
    );
  });

  submitMakePayment$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsActions.submitMakePayment),
      concatLatestFrom(() => [this.store.select(selectPaymentUUID)]),
      mergeMap(([action, uuid]) =>
        this.requestPaymentService
          .submitMakePayment({ uuid, method: action.method })
          .pipe(
            map((result: string) => {
              return submitMakePaymentSuccess({
                nextUrl: result,
              });
            }),
            catchError((httpErrorResponse: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform({
                    errorDetails: [
                      {
                        message: httpErrorResponse.message,
                      },
                    ],
                  }),
                })
              );
            })
          )
      )
    );
  });

  submitMakePaymentSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskDetailsActions.submitMakePaymentSuccess),
      concatLatestFrom(() => [this.store.select(selectPaymentMethod)]),
      map(([action, method]) => {
        if (method === 'CARD_OR_DIGITAL_WALLET') {
          return navigateToGovUKPayService(action);
        } else if (method === 'BACS') {
          return navigateToBACSDetailsPaymentMethod();
        } else throw new Error('Unknown paymment method.');
      })
    );
  });

  downloadPaymentReceiptFile$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(downloadPaymentReceipt),
        concatLatestFrom(() => [this.store.select(selectPaymentUUID)]),
        mergeMap(([, uuid]) =>
          this.requestPaymentService.downloadReceipt(uuid).pipe(
            map((result) =>
              this.exportFileService.export(
                result.body,
                this.exportFileService.getContentDispositionFilename(
                  result.headers.get('Content-Disposition')
                )
              )
            ),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          )
        )
      ),
    { dispatch: false }
  );

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
