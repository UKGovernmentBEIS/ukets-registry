import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserListRoutingModule } from './user-list-routing.module';
import { UserListContainerComponent } from './user-list-container.component';
import { SearchUsersFormComponent } from './search-users-form/search-users-form.component';
import { SearchUsersResultsComponent } from './search-users-results/search-users-results.component';
import { SharedModule } from '@shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { StoreModule } from '@ngrx/store';
import * as fromUserList from './user-list.reducer';
import { EffectsModule } from '@ngrx/effects';
import { UserListEffect } from './user-list.effect';
import { UserListResolver } from './user-list.resolver';
import { SortService } from '@shared/search/sort/sort.service';
import { UserListService } from '@user-management/service';
import { ReportsModule } from '@reports/reports.module';

@NgModule({
  declarations: [
    UserListContainerComponent,
    SearchUsersFormComponent,
    SearchUsersResultsComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    ReactiveFormsModule,
    UserListRoutingModule,
    StoreModule.forFeature(
      fromUserList.userListFeatureKey,
      fromUserList.reducer
    ),
    EffectsModule.forFeature([UserListEffect]),
    ReportsModule,
  ],
  providers: [UserListService, UserListResolver, SortService],
})
export class UserListModule {}
