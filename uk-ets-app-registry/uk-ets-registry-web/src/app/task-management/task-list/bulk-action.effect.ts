import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import * as TaskListActions from './task-list.actions';
import { exhaustMap, tap, catchError, delay, map } from 'rxjs/operators';
import { ErrorDetail } from 'src/app/shared/error-summary/error-detail';
import {
  BulkActionPayload,
  Mode,
  BulkAssignPayload,
  TaskActionErrorResponse,
  ActionError,
  BulkActionSuccess,
  apiErrorToBusinessError,
} from '@task-management/model';
import { Store, ActionCreator } from '@ngrx/store';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { canGoBack, errors } from 'src/app/shared/shared.action';
import { of, Observable } from 'rxjs';
import { TaskListRoutes } from './task-list.properties';
import { TaskService } from '@shared/services/task-service';

@Injectable()
export class BulkClaimEffect {
  constructor(
    private actions$: Actions,
    private apiService: TaskService,
    private store: Store,
    private router: Router
  ) {}

  claimTasks$ = this.createBulkEffect(TaskListActions.claimTasks, (action) =>
    this.apiService.claim(action.requestIds, action.comment)
  );

  assignTasks$ = this.createBulkEffect(
    TaskListActions.assignTasks,
    (action) => {
      const payload = action as BulkAssignPayload;
      return this.apiService.assign(
        payload.requestIds,
        payload.comment,
        payload.urid
      );
    }
  );

  resetSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskListActions.resetSuccess),
      delay(2000),
      map(() => TaskListActions.clearSuccess())
    );
  });

  createBulkEffect(
    actionCreator: ActionCreator,
    serviceMethod: (action: BulkActionPayload) => Observable<BulkActionSuccess>
  ) {
    return createEffect(
      () => {
        return this.actions$.pipe(
          ofType(actionCreator),
          exhaustMap((action: BulkActionPayload) => {
            return serviceMethod(action).pipe(
              tap((success) => {
                this.store.dispatch(
                  canGoBack({ goBackRoute: null, extras: null })
                );
                this.store.dispatch(TaskListActions.notifySuccess(success));
                this.router.navigate([TaskListRoutes.TASK_LIST], {
                  queryParams: { mode: Mode.LOAD },
                });
              }),
              catchError((httpError: HttpErrorResponse) => {
                this.handleHttpError(httpError, action.potentialErrors);
                return of([]);
              })
            );
          })
        );
      },
      {
        dispatch: false,
      }
    );
  }

  private handleHttpError(
    httpError: HttpErrorResponse,
    potentialErrors: Map<any, ErrorDetail>
  ) {
    let errorDetails: ErrorDetail[];
    if (httpError.status === 400 || httpError.status === 403) {
      errorDetails = [];
      const errorResponse: TaskActionErrorResponse = apiErrorToBusinessError(
        httpError.error
      );

      const errorMsg: string = errorResponse.message;
      if (errorMsg) {
        errorDetails.push(new ErrorDetail(null, errorMsg));
      }

      if (errorResponse.errors) {
        errorResponse.errors.forEach((bulkActionError) =>
          errorDetails.push(this.buildErrorDetail(bulkActionError))
        );
      }
    } else if (httpError.toString().includes('RPT Request denied.')) {
      errorDetails = [
        new ErrorDetail(
          null,
          'You do not have permission to perform this action.'
        ),
      ];
    } else if (potentialErrors.has(httpError.status)) {
      errorDetails = [potentialErrors.get(httpError.status)];
    } else {
      errorDetails = [potentialErrors.get('other')];
    }
    this.store.dispatch(errors({ errorSummary: { errors: errorDetails } }));
  }

  buildErrorDetail(bulkActionError: ActionError): ErrorDetail {
    let message = null;
    switch (bulkActionError.code) {
      case 'TASK_COMPLETED':
        message = `The task with request id ${bulkActionError.requestId} has been completed.`;
        break;
      case 'TASK_NOT_FOUND':
        message = `The task with request id ${bulkActionError.requestId} does not exist.`;
        break;
      case 'USER_NOT_FOUND':
        message = `User of urid ${bulkActionError.urid} does not exist.`;
        break;
      case 'NO_TASKS_SELECTED':
        message = `No tasks have been selected for this action`;
        break;
      case 'TASK_NOT_CLAIMED_BY_ASSIGNOR':
        message = `You cannot assign the task with request id ${bulkActionError.requestId}. You need to claim it first.`;
        break;
      case 'INVALID_CLAIMANT':
        message = bulkActionError.message;
        break;
      case 'ASSIGNOR_NOT_ALLOWED_TO_ASSIGN_TO_ASSIGNEE':
        message = bulkActionError.message;
        break;
      case 'ASSIGNEE_NOT_ALLOWED_TO_BE_ASSIGNED_WITH_TASK':
        message = bulkActionError.message;
        break;
      case 'COMMENT_REQUIRED':
        message = 'Comment is required';
        break;
      case 'USER_NOT_FOUND_IN_KEYCLOAK_DB':
        message = `User with urid ${bulkActionError.urid} was not found in Keycloak database.`;
        break;
      case 'CLAIM_CLAIMED_WITHOUT_COMMENT':
        message = bulkActionError.message;
        break;
      default:
        message = bulkActionError.message;
    }

    return new ErrorDetail(null, message);
  }
}
