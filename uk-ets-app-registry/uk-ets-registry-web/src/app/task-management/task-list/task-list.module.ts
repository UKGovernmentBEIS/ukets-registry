import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { BulkActionsModule } from '@shared/task-and-regulator-notice-management/bulk-actions';
import { ReportsModule } from '@reports/reports.module';
import { TaskDetailsModule } from '@task-details/task-details.module';
import * as fromTaskList from './store/task-list.reducer';
import { TaskSearchContainerComponent } from './task-search/task-search-container.component';
import { SearchTasksResultsComponent } from './task-search/search-tasks-results/search-tasks-results.component';
import { TaskListEffect } from './task-search/task-search.effect';
import { TaskSearchResolver } from './task-search/task-search.resolver';
import { SelectedTasksResolver } from './util/selected-tasks-resolver';
import { SearchTasksAdminCriteriaComponent } from './task-search/search-tasks-form/search-tasks-admin-criteria/search-tasks-admin-criteria.component';
import { SearchTasksUserCriteriaComponent } from './task-search/search-tasks-form/search-tasks-user-criteria/search-tasks-user-criteria.component';
import { TaskListRoutingModule } from './task-list-routing.module';

@NgModule({
  declarations: [
    TaskSearchContainerComponent,
    SearchTasksResultsComponent,
    SearchTasksAdminCriteriaComponent,
    SearchTasksUserCriteriaComponent,
  ],
  imports: [
    CommonModule,
    TaskListRoutingModule,
    SharedModule,
    FormsModule,
    ReactiveFormsModule,
    NgbTypeaheadModule,
    TaskDetailsModule,
    StoreModule.forFeature(
      fromTaskList.taskListFeatureKey,
      fromTaskList.reducer
    ),
    EffectsModule.forFeature([TaskListEffect]),
    SharedModule,
    ReportsModule,
    BulkActionsModule,
  ],
  providers: [TaskSearchResolver, SelectedTasksResolver],
})
export class TaskListModule {}
