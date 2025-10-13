import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { LoginGuard } from '@shared/guards';
import {
  CompleteTaskContainerComponent,
  HistoryContainerComponent,
  PaymentConfirmationContainerComponent,
  PaymentSelectMethodContainerComponent,
  PaymentBacsDetailsContainerComponent,
  TaskApprovalConfirmationContainerComponent,
  TaskDetailsContainerComponent,
  TaskUserDetailsContainerComponent,
  PaymentBacsConfirmContainerComponent,
  PaymentBacsCancelContainerComponent,
  PaymentWeblinkErrorContainerComponent,
} from '@task-details/components';
import { TaskHeaderGuard } from '@task-management/guards/task-header.guard';
import { TaskUserDetailsResolver } from '@task-details/task-user-details.resolver';
import { UploadedDocumentsGuard } from '@task-details/guards/uploaded-documents.guard';
import { TaskCompletionPendingConfirmationContainerComponent } from './components/task-completion-pending-confirmation';
import { CancelChangeTaskDeadlineContainerComponent } from './components/change-task-deadline/cancel-change-task-deadline-container.component';
import { ChangeTaskDeadlineContainerComponent } from './components/change-task-deadline/change-task-deadline-container.component';
import { ChangeDeadlineSuccessComponent } from './components/change-task-deadline/change-deadline-success/change-deadline-success.component';
import { ChangeDeadlineCheckAndSubmitContainerComponent } from './components/change-task-deadline/change-deadline-check-and-submit/change-deadline-check-and-submit-container.component';
import { TaskNotesContainerComponent } from './components/task-notes/components/task-notes-container.component';
import {
  paymentConfirmationGuard,
  paymentWeblinkConfirmationGuard,
} from '@task-details/guards';

// TODO : move canActivate:[LoginGuard] to a common parent
export const routes: Routes = [
  {
    path: ':requestId',
    canActivate: [LoginGuard, TaskHeaderGuard],
    canDeactivate: [UploadedDocumentsGuard],
    component: TaskDetailsContainerComponent,
    title: 'Task Details',
  },
  {
    path: ':requestId/notes-list',
    canActivate: [LoginGuard, TaskHeaderGuard],
    canDeactivate: [UploadedDocumentsGuard],
    component: TaskNotesContainerComponent,
  },
  {
    path: ':requestId/user/:urid',
    canActivate: [LoginGuard, TaskHeaderGuard],
    resolve: { user: TaskUserDetailsResolver },
    component: TaskUserDetailsContainerComponent,
  },
  {
    path: ':requestId/complete',
    canActivate: [LoginGuard],
    canDeactivate: [UploadedDocumentsGuard],
    component: CompleteTaskContainerComponent,
  },
  {
    path: ':requestId/approved',
    canActivate: [LoginGuard],
    component: TaskApprovalConfirmationContainerComponent,
  },
  {
    path: ':requestId/completion-pending',
    canActivate: [LoginGuard],
    canDeactivate: [UploadedDocumentsGuard],
    component: TaskCompletionPendingConfirmationContainerComponent,
  },
  {
    path: ':requestId/change-task-deadline',
    canActivate: [LoginGuard, TaskHeaderGuard],
    canDeactivate: [UploadedDocumentsGuard],
    component: ChangeTaskDeadlineContainerComponent,
  },
  {
    path: ':requestId/cancel-change-task-deadline',
    canActivate: [LoginGuard, TaskHeaderGuard],
    canDeactivate: [UploadedDocumentsGuard],
    component: CancelChangeTaskDeadlineContainerComponent,
  },
  {
    path: ':requestId/check-change-task-deadline',
    canActivate: [LoginGuard, TaskHeaderGuard],
    canDeactivate: [UploadedDocumentsGuard],
    component: ChangeDeadlineCheckAndSubmitContainerComponent,
  },
  {
    path: ':requestId/change-task-deadline-success',
    canActivate: [LoginGuard, TaskHeaderGuard],
    canDeactivate: [UploadedDocumentsGuard],
    component: ChangeDeadlineSuccessComponent,
  },
  {
    path: ':requestId/payment-select-method',
    canActivate: [LoginGuard, TaskHeaderGuard],
    // canDeactivate: [UploadedDocumentsGuard],
    component: PaymentSelectMethodContainerComponent,
  },
  {
    path: ':requestId/payment-bacs-details',
    canActivate: [LoginGuard, TaskHeaderGuard],
    component: PaymentBacsDetailsContainerComponent,
  },
  {
    path: ':requestId/payment-bacs-confirm',
    canActivate: [LoginGuard, TaskHeaderGuard],
    component: PaymentBacsConfirmContainerComponent,
  },
  {
    path: ':requestId/payment-bacs-cancel',
    canActivate: [LoginGuard, TaskHeaderGuard],
    component: PaymentBacsCancelContainerComponent,
  },
  {
    path: ':requestId/payment-confirmation',
    canActivate: [LoginGuard, TaskHeaderGuard, paymentConfirmationGuard],
    component: PaymentConfirmationContainerComponent,
  },
  {
    path: ':uuid/payment-weblink-confirmation',
    canActivate: [paymentWeblinkConfirmationGuard],
    component: PaymentConfirmationContainerComponent,
  },
  {
    path: ':uuid/payment-weblink-error',
    component: PaymentWeblinkErrorContainerComponent,
  },
  {
    path: ':requestId/history',
    canActivate: [LoginGuard],
    component: HistoryContainerComponent,
  },
  {
    path: ':requestId/notes',
    loadChildren: () =>
      import(
        './components/task-notes/task-notes-wizard/task-notes-wizard-routing.module'
      ).then((m) => m.TaskNotesRoutingModule),
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TaskDetailsRoutingModule {}
