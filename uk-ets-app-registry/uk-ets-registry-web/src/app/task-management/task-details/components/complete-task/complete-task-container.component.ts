import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import {
  CompleteTaskFormInfo,
  TaskDetails,
  TaskOutcome,
  TaskType,
} from '@task-management/model';
import { UntypedFormGroup } from '@angular/forms';
import { Store } from '@ngrx/store';
import {
  selectTask,
  selectUserDecisionForTask,
} from '@task-details/reducers/task-details.selector';
import { Observable } from 'rxjs';
import { taskTypeOptions } from '@task-management/task-list/task-list.selector';
import { TaskDetailsActions } from '@task-details/actions';
import { ErrorSummary } from '@shared/error-summary';
import { selectErrorSummary } from '@shared/shared.selector';

@Component({
  selector: 'app-complete-task-container',
  template: `
    <app-feature-header-wrapper>
      <app-task-header
        [taskDetails]="task$ | async"
        [taskHeaderActionsVisibility]="false"
        [showBackToList]="false"
        [taskTypeOptions]="taskTypeOptions$ | async"
      >
      </app-task-header>
    </app-feature-header-wrapper>
    <app-complete-task
      [taskDetails]="task$ | async"
      [taskOutcome]="userDecision$ | async"
      [errorSummary]="errorSummary$ | async"
      (completeTaskFormInfo)="onSubmitCompleteForm($event)"
      (downloadErrorsCSV)="onDownloadErrorsCSV($event)"
    >
    </app-complete-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompleteTaskContainerComponent implements OnInit {
  task$: Observable<TaskDetails>;
  taskTypeOptions$: Observable<TaskType[]>;
  userDecision$: Observable<TaskOutcome>;
  errorSummary$: Observable<ErrorSummary>;
  label: string;
  form: UntypedFormGroup;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.task$ = this.store.select(selectTask);
    this.taskTypeOptions$ = this.store.select(taskTypeOptions);
    this.userDecision$ = this.store.select(selectUserDecisionForTask);
    this.errorSummary$ = this.store.select(selectErrorSummary);
  }

  onSubmitCompleteForm($event: CompleteTaskFormInfo): void {
    this.store.dispatch(
      TaskDetailsActions.setCompleteTask({ completeTaskFormInfo: $event })
    );
  }

  onDownloadErrorsCSV(fileId: number): void {
    this.store.dispatch(
      TaskDetailsActions.fetchTaskRelatedFile({
        fileId,
      })
    );
  }
}
