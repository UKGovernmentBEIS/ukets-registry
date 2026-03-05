import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { selectTask } from '@task-details/reducers/task-details.selector';
import {
  REQUEST_TYPE_VALUES,
  TaskFileDownloadInfo,
  TaskOutcome,
} from '@shared/task-and-regulator-notice-management/model';
import { TaskDetailsActions } from '@task-details/actions';
import { taskTypeOptions } from '@task-management/task-list/store/task-list.selector';
import {
  selectGoBackToListNavigationExtras,
  selectGoBackToListRoute,
} from '@shared/shared.selector';
import {
  isAdmin,
  isSeniorAdmin,
  isSeniorOrJuniorAdmin,
} from '@registry-web/auth/auth.selector';
import { fetchAccountOpeningSummaryFile } from '@task-details/actions/task-details.actions';
import { SharedModule } from '@registry-web/shared/shared.module';
import { TaskDetailsTabsNavigationComponent } from '@task-details/components/task-details-tabs-navigation/task-details-tabs-navigation.component';
import { TaskHeaderComponent } from '@task-details/components/task-header';
import {
  selectTaskNotes,
  TaskNotesActions,
  TaskNotesComponent,
} from '@registry-web/notes/task-notes';
import { canGoBackToList } from '@registry-web/shared/shared.action';
import { SearchMode } from '@registry-web/shared/resolvers/search.resolver';

@Component({
  selector: 'app-task-notes-container',
  standalone: true,
  imports: [
    SharedModule,
    TaskHeaderComponent,
    TaskDetailsTabsNavigationComponent,
    TaskNotesComponent,
  ],
  template: `
    <app-feature-header-wrapper>
      <app-task-header
        [taskDetails]="task$ | async"
        [showBackToList]="true"
        [showExportToPDF]="true"
        [taskHeaderActionsVisibility]="true"
        [taskTypeOptions]="taskTypeOptions$ | async"
        [isAdmin]="isAdmin$ | async"
        [goBackToListRoute]="goBackToListRoute$ | async"
        [goBackToListNavigationExtras]="goBackToListNavigationExtras$ | async"
        (userDecision)="onUserDecisionForTask($event)"
        (handleExportPDF)="onExportPDF($event)"
      />
    </app-feature-header-wrapper>
    <app-task-details-tabs-navigation
      [requestId]="requestId"
      [isAdmin]="isAdmin$ | async"
    />
    <app-task-notes
      [requestId]="requestId"
      [isAdmin]="isSeniorOrJuniorAdmin$ | async"
      [isSeniorAdmin]="isSeniorAdmin$ | async"
      [notes]="notes$ | async"
    />
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskNotesContainerComponent implements OnInit {
  readonly task$ = this.store.select(selectTask);
  readonly isAdmin$ = this.store.select(isAdmin);
  readonly isSeniorAdmin$ = this.store.select(isSeniorAdmin);
  readonly isSeniorOrJuniorAdmin$ = this.store.select(isSeniorOrJuniorAdmin);
  readonly taskTypeOptions$ = this.store.select(taskTypeOptions);
  readonly goBackToListRoute$ = this.store.select(selectGoBackToListRoute);
  readonly goBackToListNavigationExtras$ = this.store.select(
    selectGoBackToListNavigationExtras
  );
  readonly notes$ = this.store.select(selectTaskNotes);

  requestId: string;

  constructor(
    private route: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe((paramMap) => {
      this.requestId = paramMap.get('requestId');
      this.store.dispatch(
        TaskNotesActions.FETCH_TASK_NOTES({ requestId: this.requestId })
      );
    });

    this.store.dispatch(
      canGoBackToList({
        goBackToListRoute: `/task-list`,
        extras: {
          skipLocationChange: false,
          queryParams: {
            mode: SearchMode.LOAD,
          },
        },
      })
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
