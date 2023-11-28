import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import {
  selectIsETSTransaction,
  selectIsTransactionReversal,
  selectLoggedUser,
  selectTask,
} from '@task-details/reducers/task-details.selector';
import {
  REQUEST_TYPE_VALUES,
  TaskDetails,
  TaskFileDownloadInfo,
  TaskOutcome,
  TaskType,
  TaskUpdateDetails,
} from '@task-management/model';
import { AuthModel } from '@registry-web/auth/auth.model';
import { TaskDetailsActions } from '@task-details/actions';
import { FileDetails } from '@shared/model/file/file-details.model';
import { Configuration } from '@shared/configuration/configuration.interface';
import { taskTypeOptions } from '@task-management/task-list/task-list.selector';
import {
  selectConfigurationRegistry,
  selectGoBackToListNavigationExtras,
  selectGoBackToListRoute,
} from '@shared/shared.selector';
import { enterRequestDocumentsWizard } from '@request-documents/wizard/actions';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { GoBackNavigationExtras } from '@shared/back-button';
import { fetchAccountOpeningSummaryFile } from '../../actions/task-details.actions';

@Component({
  selector: 'app-task-details-container',
  template: `
    <app-feature-header-wrapper>
      <app-task-header
        [taskDetails]="task$ | async"
        [showBackToList]="true"
        [showExportToPDF]="true"
        [taskHeaderActionsVisibility]="true"
        [taskTypeOptions]="taskTypeOptions$ | async"
        [isAdmin]="isAdmin$ | async"
        (userDecision)="onUserDecisionForTask($event)"
        (handleExportPDF)="onExportPDF($event)"
        [goBackToListRoute]="goBackToListRoute$ | async"
        [goBackToListNavigationExtras]="goBackToListNavigationExtras$ | async"
      ></app-task-header>
    </app-feature-header-wrapper>
    <app-task-details
      [isAdmin]="isAdmin$ | async"
      [configuration]="configuration$ | async"
      [isEtsTransaction]="isEtsTransaction$ | async"
      [isTransactionReversal]="isTransactionReversal$ | async"
      [loggedInUser]="loggedInUser$ | async"
      [taskDetails]="task$ | async"
      (downloadFileTemplateEmitter)="downloadTemplate($event)"
      (downloadRequestDocumentFile)="downloadRequestDocumentFile($event)"
      (requestDocumentEmitter)="onAccountHolderOrUserRequestDocuments($event)"
      [taskTypeOptions]="taskTypeOptions$ | async"
      (openDetail)="onOpenTaskDetail($event)"
      (userDecision)="onUserDecisionForTask($event)"
    ></app-task-details>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskDetailsContainerComponent implements OnInit {
  task$: Observable<TaskDetails>;
  loggedInUser$: Observable<AuthModel>;
  isAdmin$: Observable<boolean>;
  isEtsTransaction$: Observable<boolean>;
  isTransactionReversal$: Observable<boolean>;
  requestId: string;
  configuration$: Observable<Configuration[]>;
  taskTypeOptions$: Observable<TaskType[]>;
  goBackToListRoute$: Observable<string>;
  goBackToListNavigationExtras$: Observable<GoBackNavigationExtras>;

  constructor(
    private route: ActivatedRoute,
    private store: Store,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.task$ = this.store.select(selectTask);
    this.configuration$ = this.store.select(selectConfigurationRegistry);
    this.loggedInUser$ = this.store.select(selectLoggedUser);
    this.isAdmin$ = this.store.select(isAdmin);
    this.isEtsTransaction$ = this.store.select(selectIsETSTransaction);
    this.isTransactionReversal$ = this.store.select(
      selectIsTransactionReversal
    );
    // TODO: remove this
    this.route.paramMap.subscribe((paramMap) => {
      this.requestId = paramMap.get('requestId');
    });
    this.taskTypeOptions$ = this.store.select(taskTypeOptions);

    this.goBackToListRoute$ = this.store.select(selectGoBackToListRoute);

    this.goBackToListNavigationExtras$ = this.store.select(
      selectGoBackToListNavigationExtras
    );
  }

  downloadTemplate(fileDetails: FileDetails) {
    this.store.dispatch(
      TaskDetailsActions.fetchTaskRelatedFile({
        fileId: fileDetails.id,
      })
    );
  }

  downloadRequestDocumentFile(taskFileInfo: TaskFileDownloadInfo) {
    this.store.dispatch(
      TaskDetailsActions.fetchTaskUserFile({
        taskFileDownloadInfo: taskFileInfo,
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
    console.log(taskFileDownloadInfo);
    this.store.dispatch(
      fetchAccountOpeningSummaryFile({ taskFileDownloadInfo })
    );
  }

  onOpenTaskDetail($event: string) {
    this.store.dispatch(
      TaskDetailsActions.loadTaskFromList({ taskId: $event })
    );
  }

  onAccountHolderOrUserRequestDocuments(requestDocumentDetails) {
    this.store.dispatch(
      enterRequestDocumentsWizard({
        originatingPath: this.router.url,
        ...requestDocumentDetails,
      })
    );
  }
}
