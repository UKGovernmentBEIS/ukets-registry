import { LoginGuard } from '@shared/guards';
import { TaskSearchContainerComponent } from './task-search/task-search-container.component';
import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { TaskSearchResolver } from './task-search/task-search.resolver';
import { BulkClaimComponent } from './bulk-claim/bulk-claim.component';
import { BulkAssignComponent } from './bulk-assign/bulk-assign.component';
import { SelectedTasksResolver } from './util/selected-tasks-resolver';
import {
  createTaskListErrorMap,
  createBulkClaimErrorMap,
  createBulkAssignErrorMap,
} from './util/potential-error-map.factory';

export const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: TaskSearchContainerComponent,
    resolve: { search: TaskSearchResolver },
    data: {
      errorMap: createTaskListErrorMap(),
    },
    title: 'Tasks',
  },
  {
    path: 'bulk-claim',
    canActivate: [LoginGuard],
    component: BulkClaimComponent,
    resolve: {
      selectedTasks: SelectedTasksResolver,
    },
    data: {
      errorMap: createBulkClaimErrorMap(),
    },
  },

  {
    path: 'bulk-assign',
    canActivate: [LoginGuard],
    component: BulkAssignComponent,
    resolve: {
      selectedTasks: SelectedTasksResolver,
    },
    data: {
      errorMap: createBulkAssignErrorMap(),
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TaskListRoutingModule {}
