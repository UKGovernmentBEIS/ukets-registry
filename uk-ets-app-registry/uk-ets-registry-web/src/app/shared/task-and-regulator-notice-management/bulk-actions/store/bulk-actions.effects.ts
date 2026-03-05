import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import {
  exhaustMap,
  tap,
  catchError,
  delay,
  map,
  withLatestFrom,
} from 'rxjs/operators';
import { ErrorDetail } from '@shared/error-summary/error-detail';
import { canGoBack, errors } from '@shared/shared.action';
import { TaskService } from '@shared/services/task-service';
import { BulkActions } from '@shared/task-and-regulator-notice-management/bulk-actions/store/bulk-actions.actions';
import {
  ActionError,
  apiErrorToBusinessError,
  ListMode,
  TaskActionErrorResponse,
} from '@shared/task-and-regulator-notice-management/model';
import { Store } from '@ngrx/store';
import {
  selectItemTypeLabel,
  selectListPath,
} from '@shared/task-and-regulator-notice-management/bulk-actions/store/bulk-actions.selectors';
import { BulkActionsConfig } from '../bulk-actions.model';

@Injectable()
export class BulkActionsEffects {
  private readonly actions$ = inject(Actions);
  private readonly store = inject(Store);
  private readonly apiService = inject(TaskService);
  private readonly router = inject(Router);

  bulkClaim$ = createEffect(() =>
    this.actions$.pipe(
      ofType(BulkActions.BULK_CLAIM),
      withLatestFrom(this.store.select(selectItemTypeLabel)),
      exhaustMap(([{ payload }, itemTypeLabel]) => {
        return this.apiService.claim(payload.requestIds, payload.comment).pipe(
          map((success) => BulkActions.BULK_CLAIM_SUCCESS(success)),
          catchError((httpError: HttpErrorResponse) =>
            this.handleHttpError(
              httpError,
              payload.potentialErrors,
              itemTypeLabel
            )
          )
        );
      })
    )
  );

  bulkAssign$ = createEffect(() =>
    this.actions$.pipe(
      ofType(BulkActions.BULK_ASSIGN),
      withLatestFrom(this.store.select(selectItemTypeLabel)),
      exhaustMap(([{ payload }, itemTypeLabel]) => {
        return this.apiService
          .assign(payload.requestIds, payload.comment, payload.urid)
          .pipe(
            map((success) => BulkActions.BULK_ASSIGN_SUCCESS(success)),
            catchError((httpError: HttpErrorResponse) =>
              this.handleHttpError(
                httpError,
                payload.potentialErrors,
                itemTypeLabel
              )
            )
          );
      })
    )
  );

  showSuccessMessage$ = createEffect(() =>
    this.actions$.pipe(
      ofType(BulkActions.BULK_CLAIM_SUCCESS, BulkActions.BULK_ASSIGN_SUCCESS),
      map(({ payload }) => BulkActions.SHOW_SUCCESS_MESSAGE(payload))
    )
  );

  navigateOnSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(BulkActions.SHOW_SUCCESS_MESSAGE),
      withLatestFrom(this.store.select(selectListPath)),
      tap(([, listPath]) =>
        this.router.navigate(['/' + listPath], {
          queryParams: { mode: ListMode.LOAD },
        })
      ),
      map(() => canGoBack({ goBackRoute: null, extras: null }))
    )
  );

  resetSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(BulkActions.RESET_SUCCESS),
      delay(2000),
      map(() => BulkActions.CLEAR_SUCCESS())
    )
  );

  private handleHttpError(
    httpError: HttpErrorResponse,
    potentialErrors: Map<any, ErrorDetail>,
    itemTypeLabel: BulkActionsConfig['itemTypeLabel']
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
          errorDetails.push(
            this.buildErrorDetail(bulkActionError, itemTypeLabel)
          )
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
    return of(errors({ errorSummary: { errors: errorDetails } }));
  }

  private buildErrorDetail(
    bulkActionError: ActionError,
    itemTypeLabel: BulkActionsConfig['itemTypeLabel']
  ): ErrorDetail {
    let message = null;
    switch (bulkActionError.code) {
      case 'TASK_COMPLETED':
        message = `The ${itemTypeLabel} with request id ${bulkActionError.requestId} has been completed.`;
        break;
      case 'TASK_NOT_FOUND':
        message = `The ${itemTypeLabel} with request id ${bulkActionError.requestId} does not exist.`;
        break;
      case 'USER_NOT_FOUND':
        message = `User of urid ${bulkActionError.urid} does not exist.`;
        break;
      case 'NO_TASKS_SELECTED':
        message = `No ${itemTypeLabel}s have been selected for this action`;
        break;
      case 'TASK_NOT_CLAIMED_BY_ASSIGNOR':
        message = `You cannot assign the ${itemTypeLabel} with request id ${bulkActionError.requestId}. You need to claim it first.`;
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
