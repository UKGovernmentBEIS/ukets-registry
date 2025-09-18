import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { TransactionListContainerComponent } from './transaction-list-container.component';
import { LoginGuard } from '@shared/guards';
import { createTransactionListErrorMap } from './potential-error-map.factory';
import { TransactionListResolver } from './transaction-list.resolver';
import { FiltersDescriptorResolver } from './filters-descriptor.resolver';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: TransactionListContainerComponent,
    resolve: {
      search: TransactionListResolver,
      filtersDescriptor: FiltersDescriptorResolver,
    },
    data: {
      errorMap: createTransactionListErrorMap(),
    },
    title: 'Transactions',
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TransactionListRoutingModule {}
