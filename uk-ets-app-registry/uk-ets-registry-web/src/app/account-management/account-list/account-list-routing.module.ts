import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AccountListContainerComponent } from './account-list-container.component';
import { FiltersDescriptorResolver } from './filters-descriptor.resolver';
import { createAccountListErrorMap } from './potential-error-map.factory';
import { AccountListResolver } from './account-list.resolver';

const routes: Routes = [
  {
    path: '',
    resolve: {
      filtersDescriptor: FiltersDescriptorResolver,
      search: AccountListResolver
    },
    component: AccountListContainerComponent,
    data: {
      errorMap: createAccountListErrorMap()
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AccountListRoutingModule {}
