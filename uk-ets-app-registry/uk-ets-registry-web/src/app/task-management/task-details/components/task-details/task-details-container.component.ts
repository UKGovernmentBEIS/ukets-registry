/* eslint-disable @angular-eslint/component-max-inline-declarations */
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import {
  selectIsETSTransaction,
  selectIsTransactionReversal,
  selectLoggedUser,
  selectTask,
} from '@task-details/reducers/task-details.selector';
import {
  REQUEST_TYPE_VALUES,
  TaskFileDownloadInfo,
  TaskOutcome,
} from '@task-management/model';
import { TaskDetailsActions } from '@task-details/actions';
import { FileDetails } from '@shared/model/file/file-details.model';
import { taskTypeOptions } from '@task-management/task-list/task-list.selector';
import {
  selectConfigurationRegistry,
  selectGoBackToListNavigationExtras,
  selectGoBackToListRoute,
} from '@shared/shared.selector';
import { enterRequestDocumentsWizard } from '@request-documents/wizard/actions';
import {
  isAdmin,
  isSeniorOrJuniorAdmin,
} from '@registry-web/auth/auth.selector';
import { fetchAccountOpeningSummaryFile } from '../../actions/task-details.actions';
import { enterRequestPaymentWizard } from '@request-payment/store/actions';
import { navigateAndLoadPaymentList } from '@registry-web/payment-management/payment-list/store/actions';
import { navigateToGovUKPayService } from '../../actions/task-details-navigation.actions';
import { fetchTaskNotes } from '@task-details/components/task-notes/store/task-notes.actions';

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
    <app-task-details-tabs-navigation
      [requestId]="requestId"
      [isAdmin]="isAdmin$ | async"
    ></app-task-details-tabs-navigation>
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
      (requestPaymentEmitter)="onRequestPayment($event)"
      (navigateToPaymentsListEmitter)="onNavigateToPaymentsList($event)"
      (navigateToGovUKPayEmitter)="onNavigateToGovUKPay($event)"
      [taskTypeOptions]="taskTypeOptions$ | async"
      [isSeniorOrJuniorAdmin]="isSeniorOrJuniorAdmin$ | async"
      (openDetail)="onOpenTaskDetail($event)"
      (userDecision)="onUserDecisionForTask($event)"
    ></app-task-details>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TaskDetailsContainerComponent implements OnInit {
  task$ = this.store.select(selectTask);
  loggedInUser$ = this.store.select(selectLoggedUser);
  isAdmin$ = this.store.select(isAdmin);
  isEtsTransaction$ = this.store.select(selectIsETSTransaction);
  isTransactionReversal$ = this.store.select(selectIsTransactionReversal);
  requestId: string;
  configuration$ = this.store.select(selectConfigurationRegistry);
  taskTypeOptions$ = this.store.select(taskTypeOptions);
  goBackToListRoute$ = this.store.select(selectGoBackToListRoute);
  goBackToListNavigationExtras$ = this.store.select(
    selectGoBackToListNavigationExtras
  );
  isSeniorOrJuniorAdmin$ = this.store.select(isSeniorOrJuniorAdmin);

  constructor(
    private route: ActivatedRoute,
    private store: Store,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.store.dispatch(TaskDetailsActions.resetSubmittedApproveTask());

    // TODO: remove this
    this.route.paramMap.subscribe((paramMap) => {
      this.requestId = paramMap.get('requestId');
    });

    this.store.dispatch(
      fetchTaskNotes({
        requestId: this.requestId,
      })
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

  onRequestPayment(requestPaymentWizardDetails) {
    this.store.dispatch(
      enterRequestPaymentWizard({
        originatingPath: this.router.url,
        ...requestPaymentWizardDetails,
      })
    );
  }

  onNavigateToPaymentsList(referenceNumber) {
    this.store.dispatch(navigateAndLoadPaymentList(referenceNumber));
  }

  onNavigateToGovUKPay(nextUrl) {
    this.store.dispatch(navigateToGovUKPayService(nextUrl));
  }
}
