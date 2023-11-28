import { Injectable } from '@angular/core';
import { IssueKpUnitsService } from '@issue-kp-units/services';
import { Actions, concatLatestFrom, createEffect, ofType } from '@ngrx/effects';
import { ActivatedRoute, Router, RouterStateSnapshot } from '@angular/router';
import { filter, map, pairwise, tap, withLatestFrom } from 'rxjs/operators';
import {
  TaskDetailsActions,
  TaskDetailsApiActions,
  TaskDetailsNavigationActions,
} from '@task-details/actions';
import { select, Store } from '@ngrx/store';
import {
  selectHasUploadedFiles,
  selectTask,
} from '@task-details/reducers/task-details.selector';
import { ROUTER_NAVIGATION, RouterAction } from '@ngrx/router-store';
import { RequestType } from '@registry-web/task-management/model';
import { selectLoggedInUser } from '@registry-web/auth/auth.selector';

@Injectable()
export class TaskDetailsNavigationEffects {
  constructor(
    private actions$: Actions,
    private _router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  navigateToCompleteTaskWhen$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        TaskDetailsActions.rejectTaskDecision,
        TaskDetailsActions.approveTaskDecision
      ),
      map(() => TaskDetailsNavigationActions.navigateToCompleteTask())
    )
  );

  navigateToTaskDetailsWhen$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        TaskDetailsApiActions.completeTaskWithRejectionSuccess,
        TaskDetailsActions.loadTaskFromListSuccess
      ),
      map(() =>
        TaskDetailsNavigationActions.navigateToTaskDetails({
          extras: { skipLocationChange: true },
        })
      )
    )
  );

  navigateFromList = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskDetailsActions.loadTaskFromListSuccess),
      map(() =>
        TaskDetailsNavigationActions.navigateToTaskDetails({
          extras: { skipLocationChange: false },
        })
      )
    )
  );

  navigateToTaskDetails$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(TaskDetailsNavigationActions.navigateToTaskDetails),
        withLatestFrom(this.store.pipe(select(selectTask))),
        tap(([action, task]) =>
          this._router.navigate(
            [`/task-details/${task.requestId}`],
            action.extras
          )
        )
      ),
    { dispatch: false }
  );

  navigatateToCompleteTask$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(TaskDetailsNavigationActions.navigateToCompleteTask),
        withLatestFrom(this.store.pipe(select(selectTask))),
        tap(([action, task]) =>
          this._router.navigate([`/task-details/${task.requestId}/complete`], {
            relativeTo: this.activatedRoute,
            skipLocationChange: true,
          })
        )
      ),
    { dispatch: false }
  );

  navigateToTaskApproved$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(TaskDetailsNavigationActions.navigateToTaskApproved),
        withLatestFrom(this.store.pipe(select(selectTask))),
        tap(([action, task]) =>
          this._router.navigate([`/task-details/${task.requestId}/approved`], {
            relativeTo: this.activatedRoute,
            skipLocationChange: true,
          })
        )
      ),
    { dispatch: false }
  );

  navigateToTaskCompletionPending$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(
          TaskDetailsNavigationActions.navigateToTaskCompletionPending,
          TaskDetailsNavigationActions.navigationAwayTargetURL
        ),
        concatLatestFrom(() => this.store.select(selectTask)),
        tap(([action, task]) =>
          this._router.navigate(
            [`/task-details/${task.requestId}/completion-pending`],
            {
              relativeTo: this.activatedRoute,
              skipLocationChange: true,
            }
          )
        )
      );
    },
    { dispatch: false }
  );

  navigateAway$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ROUTER_NAVIGATION),
      pairwise(),
      concatLatestFrom(() => [
        this.store.select(selectTask),
        this.store.select(selectHasUploadedFiles),
        this.store.select(selectLoggedInUser),
      ]),
      filter(
        ([[previousAction, currentAction], task, hasUploadedFiles, user]) => {
          const isDocumentRequestTask =
            task.taskType === RequestType.AR_REQUESTED_DOCUMENT_UPLOAD ||
            task.taskType === RequestType.AH_REQUESTED_DOCUMENT_UPLOAD;
          const isTaskComplete = task.taskStatus === 'COMPLETED';
          const taskDetailsUrlRegex = new RegExp('^(/task-details/[0-9]+/*)$');
          const previousUrl = (
            previousAction as RouterAction<RouterStateSnapshot>
          ).payload.event.url;
          const isPreviousRouteTaskDetails =
            taskDetailsUrlRegex.test(previousUrl);

          const isClaimant = user.urid === task.claimantURID;

          return (
            hasUploadedFiles &&
            isDocumentRequestTask &&
            !isTaskComplete &&
            isPreviousRouteTaskDetails &&
            isClaimant
          );
        }
      ),
      map(([[previousAction, currentAction]]) => {
        const currentUrl = (currentAction as RouterAction<RouterStateSnapshot>)
          .payload.routerState.url;
        return TaskDetailsNavigationActions.navigationAwayTargetURL({
          url: currentUrl,
        });
      })
    );
  });
}
