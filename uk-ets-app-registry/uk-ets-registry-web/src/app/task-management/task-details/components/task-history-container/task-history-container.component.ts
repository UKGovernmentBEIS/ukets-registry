import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { map } from 'rxjs';
import { Store } from '@ngrx/store';
import {
  selectTask,
  selectTaskHistory,
} from '@task-details/reducers/task-details.selector';
import {
  REQUEST_TYPE_VALUES,
  TaskFileDownloadInfo,
  TaskOutcome,
} from '@shared/task-and-regulator-notice-management/model';
import { taskTypeOptions } from '@task-management/task-list/store/task-list.selector';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { navigateToUserProfile } from '@shared/shared.action';
import { TaskDetailsActions } from '@task-details/actions';
import { fetchAccountOpeningSummaryFile } from '../../actions/task-details.actions';
import {
  selectGoBackToListNavigationExtras,
  selectGoBackToListRoute,
} from '@registry-web/shared/shared.selector';

@Component({
  selector: 'app-task-history-container',
  template: `
    <app-feature-header-wrapper>
      <app-task-header
        [taskDetails]="task$ | async"
        [showBackToList]="true"
        [showExportToPDF]="true"
        [taskHeaderActionsVisibility]="true"
        [isAdmin]="isAdmin$ | async"
        [taskTypeOptions]="taskTypeOptions$ | async"
        (userDecision)="onUserDecisionForTask($event)"
        (handleExportPDF)="onExportPDF($event)"
        [goBackToListRoute]="goBackToListRoute$ | async"
        [goBackToListNavigationExtras]="goBackToListNavigationExtras$ | async"
      ></app-task-header>
    </app-feature-header-wrapper>
    <app-task-details-tabs-navigation
      [requestId]="requestId"
      [isAdmin]="isAdmin$ | async"
    ></app-task-details-tabs-navigation>
    <app-history-comments-form
      [task]="task$ | async"
      (addComment)="onAddComment($event)"
    />
    <app-domain-events
      [domainEvents]="taskHistory$ | async"
      [isAdmin]="isAdmin$ | async"
      (navigate)="navigateToUserPage($event)"
    ></app-domain-events>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HistoryContainerComponent {
  readonly taskHistory$ = this.store.select(selectTaskHistory);
  readonly task$ = this.store.select(selectTask);
  readonly taskTypeOptions$ = this.store.select(taskTypeOptions);
  readonly isAdmin$ = this.store.select(isAdmin);
  readonly goBackToListRoute$ = this.store.select(selectGoBackToListRoute);
  readonly goBackToListNavigationExtras$ = this.store.select(
    selectGoBackToListNavigationExtras
  );

  requestId: string;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {
    this.activatedRoute.paramMap.subscribe((paramMap) => {
      this.requestId = paramMap.get('requestId');
      this.store.dispatch(
        TaskDetailsActions.fetchTaskHistory({ requestId: this.requestId })
      );
    });
  }

  onAddComment({ comment, requestId }: { comment: string; requestId: string }) {
    this.store.dispatch(
      TaskDetailsActions.taskHistoryAddComment({ comment, requestId })
    );
  }

  navigateToUserPage(urid: string) {
    const goBackRoute = this.router.url;
    const userProfileRoute = '/user-details/' + urid;
    this.store.dispatch(
      navigateToUserProfile({ goBackRoute, userProfileRoute })
    );
  }

  onUserDecisionForTask({ taskOutcome, taskType }) {
    switch (taskOutcome) {
      case TaskOutcome.APPROVED:
        if (REQUEST_TYPE_VALUES[taskType].completeOnly) {
          this.store.dispatch(
            TaskDetailsActions.approveTaskDecisionForCompleteOnlyTask({
              userDecision: taskOutcome,
            })
          );
        } else {
          this.store.dispatch(
            TaskDetailsActions.approveTaskDecision({
              userDecision: taskOutcome,
            })
          );
        }
        break;
      case TaskOutcome.REJECTED:
        this.store.dispatch(
          TaskDetailsActions.rejectTaskDecision({
            userDecision: taskOutcome,
          })
        );
        break;
    }
  }

  onExportPDF(taskFileDownloadInfo: TaskFileDownloadInfo) {
    this.store.dispatch(
      fetchAccountOpeningSummaryFile({ taskFileDownloadInfo })
    );
  }
}
