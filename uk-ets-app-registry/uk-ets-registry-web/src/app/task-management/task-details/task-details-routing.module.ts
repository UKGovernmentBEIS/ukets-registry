import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { LoginGuard } from '@shared/guards';
import {
  CompleteTaskContainerComponent,
  HistoryContainerComponent,
  TaskApprovalConfirmationContainerComponent,
  TaskDetailsContainerComponent,
  TaskUserDetailsContainerComponent,
} from '@task-details/components';
import { TaskHeaderGuard } from '@task-management/guards/task-header.guard';
import { TaskUserDetailsResolver } from '@task-details/task-user-details.resolver';
import { UploadedDocumentsGuard } from '@task-details/guards/uploaded-documents.guard';
import { TaskCompletionPendingConfirmationContainerComponent } from './components/task-completion-pending-confirmation';

// TODO : move canActivate:[LoginGuard] to a common parent
const routes: Routes = [
  {
    path: ':requestId',
    canActivate: [LoginGuard, TaskHeaderGuard],
    canDeactivate: [UploadedDocumentsGuard],
    component: TaskDetailsContainerComponent,
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
    path: 'history/:requestId',
    canActivate: [LoginGuard],
    component: HistoryContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TaskDetailsRoutingModule {}
