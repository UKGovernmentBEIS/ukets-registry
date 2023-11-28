import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TransactionListRoutingModule } from './transaction-list-routing.module';
import { TransactionListContainerComponent } from './transaction-list-container.component';
import { SearchTransactionsFormComponent } from './search-transactions-form/search-transactions-form.component';
import { SharedModule } from '@shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TransactionManagementService } from '../service/transaction-management.service';
import { StoreModule } from '@ngrx/store';
import * as fromTransactionList from './transaction-list.reducer';
import { EffectsModule } from '@ngrx/effects';
import { TransactionListEffect } from './transaction-list.effect';
import { TransactionListResolver } from './transaction-list.resolver';
import { SortService } from '@shared/search/sort/sort.service';
import { FiltersDescriptorResolver } from './filters-descriptor.resolver';
import { ReportsModule } from '@reports/reports.module';

@NgModule({
  declarations: [
    TransactionListContainerComponent,
    SearchTransactionsFormComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    FormsModule,
    ReactiveFormsModule,
    TransactionListRoutingModule,
    StoreModule.forFeature(
      fromTransactionList.transactionListFeatureKey,
      fromTransactionList.reducer
    ),
    EffectsModule.forFeature([TransactionListEffect]),
    ReportsModule,
  ],
  providers: [
    TransactionManagementService,
    TransactionListResolver,
    SortService,
    FiltersDescriptorResolver,
  ],
})
export class TransactionListModule {}
