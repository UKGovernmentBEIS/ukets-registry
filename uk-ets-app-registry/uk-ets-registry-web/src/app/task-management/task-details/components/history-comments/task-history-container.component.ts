import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable, map } from 'rxjs';
import { Store } from '@ngrx/store';
import {
  selectTask,
  selectTaskHistory,
} from '@task-details/reducers/task-details.selector';
import { DomainEvent } from '@shared/model/event';
import {
  REQUEST_TYPE_VALUES,
  TaskDetails,
  TaskFileDownloadInfo,
  TaskOutcome,
  TaskType,
} from '@task-management/model';
import { taskTypeOptions } from '@task-management/task-list/task-list.selector';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { navigateToUserProfile } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import { TaskDetailsActions } from '@task-details/actions';
import { fetchAccountOpeningSummaryFile } from '../../actions/task-details.actions';
import {
  selectGoBackToListNavigationExtras,
  selectGoBackToListRoute,
} from '@registry-web/shared/shared.selector';
import { GoBackNavigationExtras } from '@registry-web/shared/back-button';

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
      [requestId]="requestId$ | async"
      [isAdmin]="isAdmin$ | async"
    ></app-task-details-tabs-navigation>
    <app-history-comments [task]="task$ | async"></app-history-comments>
    <app-domain-events
      [domainEvents]="taskHistory$ | async"
      [isAdmin]="isAdmin$ | async"
      (navigate)="navigateToUserPage($event)"
    ></app-domain-events>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HistoryContainerComponent implements OnInit {
  taskHistory$: Observable<DomainEvent[]>;
  taskTypeOptions$: Observable<TaskType[]>;
  task$: Observable<TaskDetails> = this.store.select(selectTask);
  isAdmin$: Observable<boolean>;
  requestId$: Observable<string>;
  goBackToListRoute$: Observable<string>;
  goBackToListNavigationExtras$: Observable<GoBackNavigationExtras>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.taskHistory$ = this.store.select(selectTaskHistory);
    this.task$ = this.store.select(selectTask);
    this.taskTypeOptions$ = this.store.select(taskTypeOptions);
    this.isAdmin$ = this.store.select(isAdmin);
    this.requestId$ = this.task$.pipe(map((task) => task.requestId));
    this.goBackToListRoute$ = this.store.select(selectGoBackToListRoute);
    this.goBackToListNavigationExtras$ = this.store.select(
      selectGoBackToListNavigationExtras
    );
  }

  navigateToUserPage(urid: string) {
    const goBackRoute = this.route.snapshot['_routerState'].url;
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
