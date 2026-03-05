import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { LoginGuard } from '@registry-web/shared/guards';
import {
  BULK_ASSIGN_PATH,
  BULK_CLAIM_PATH,
  BulkAssignComponent,
  BulkClaimComponent,
  createBulkAssignErrorMap,
  createBulkClaimErrorMap,
} from '@shared/task-and-regulator-notice-management/bulk-actions';
import { TaskSearchContainerComponent } from './task-search/task-search-container.component';
import { TaskSearchResolver } from './task-search/task-search.resolver';
import { SelectedTasksResolver } from './util/selected-tasks-resolver';
import { createTaskListErrorMap } from './util/potential-error-map.factory';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: TaskSearchContainerComponent,
    resolve: { search: TaskSearchResolver },
    data: { errorMap: createTaskListErrorMap() },
    title: 'Tasks',
  },
  {
    path: BULK_CLAIM_PATH,
    canActivate: [LoginGuard],
    component: BulkClaimComponent,
    resolve: { selectedTasks: SelectedTasksResolver },
    data: { errorMap: createBulkClaimErrorMap('task') },
  },
  {
    path: BULK_ASSIGN_PATH,
    canActivate: [LoginGuard],
    component: BulkAssignComponent,
    resolve: { selectedTasks: SelectedTasksResolver },
    data: { errorMap: createBulkAssignErrorMap('task') },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TaskListRoutingModule {}
