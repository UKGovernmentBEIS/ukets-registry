import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountListRoutingModule } from './account-list-routing.module';
import { AccountListContainerComponent } from './account-list-container.component';
import { SearchAccountsFormComponent } from './search-accounts-form/search-accounts-form.component';
import { AccountApiService } from '../service/account-api.service';
import { FiltersDescriptorResolver } from './filters-descriptor.resolver';
import { SearchAccountsResultsComponent } from './search-accounts-results/search-accounts-results.component';
import { SharedModule } from '../../shared/shared.module';
import * as fromAccountList from './account-list.reducer';
import { StoreModule } from '@ngrx/store';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { EffectsModule } from '@ngrx/effects';
import { AccountListEffect } from './account-list.effect';
import { AccountListResolver } from './account-list.resolver';
import { ReportsModule } from '@reports/reports.module';

@NgModule({
  declarations: [
    AccountListContainerComponent,
    SearchAccountsFormComponent,
    SearchAccountsResultsComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    ReactiveFormsModule,
    AccountListRoutingModule,
    StoreModule.forFeature(
      fromAccountList.accountListFeatureKey,
      fromAccountList.reducer
    ),
    EffectsModule.forFeature([AccountListEffect]),
    ReportsModule,
  ],
  providers: [
    AccountApiService,
    FiltersDescriptorResolver,
    AccountListResolver,
  ],
})
export class AccountListModule {}
