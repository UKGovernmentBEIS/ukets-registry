import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';
import * as fromTaskList from './task-list.reducer';
import { TaskSearchContainerComponent } from './task-search/task-search-container.component';
import { SearchTasksResultsComponent } from './task-search/search-tasks-results/search-tasks-results.component';
import { EffectsModule } from '@ngrx/effects';
import { TaskListEffect } from './task-search/task-search.effect';
import { ClaimAssignButtonGroupComponent } from './task-search/claim-assign-button-group/claim-assign-button-group.component';
import { TaskListRoutingModule } from './task-list-routing.module';
import { TaskSearchResolver } from './task-search/task-search.resolver';
import { BulkClaimComponent } from './bulk-claim/bulk-claim.component';
import { BulkAssignComponent } from './bulk-assign/bulk-assign.component';
import { SelectedTasksResolver } from './util/selected-tasks-resolver';
import { BulkClaimEffect } from './bulk-action.effect';

import { BulkActionSuccessComponent } from './bulk-action-success/bulk-action-success.component';
import { NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
// eslint-disable-next-line max-len
import { SearchTasksAdminCriteriaComponent } from './task-search/search-tasks-form/search-tasks-admin-criteria/search-tasks-admin-criteria.component';
// eslint-disable-next-line max-len
import { SearchTasksUserCriteriaComponent } from './task-search/search-tasks-form/search-tasks-user-criteria/search-tasks-user-criteria.component';
import { TaskDetailsModule } from '@task-details/task-details.module';
import { ReportsModule } from '@reports/reports.module';

@NgModule({
  declarations: [
    TaskSearchContainerComponent,
    SearchTasksResultsComponent,
    ClaimAssignButtonGroupComponent,
    BulkClaimComponent,
    BulkAssignComponent,
    BulkActionSuccessComponent,
    SearchTasksAdminCriteriaComponent,
    SearchTasksUserCriteriaComponent,
  ],
  imports: [
    NgbTypeaheadModule,
    TaskListRoutingModule,
    CommonModule,
    FormsModule,
    SharedModule,
    ReactiveFormsModule,
    TaskDetailsModule,
    StoreModule.forFeature(
      fromTaskList.taskListFeatureKey,
      fromTaskList.reducer
    ),
    EffectsModule.forFeature([TaskListEffect, BulkClaimEffect]),
    SharedModule,
    ReportsModule,
  ],

  providers: [TaskSearchResolver, SelectedTasksResolver],
})
export class TaskListModule {}
