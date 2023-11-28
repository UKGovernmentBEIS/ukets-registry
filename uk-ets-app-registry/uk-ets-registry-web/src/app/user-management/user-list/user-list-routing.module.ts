import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { UserListContainerComponent } from './user-list-container.component';
import { LoginGuard } from '@shared/guards/login.guard';
import { createUserListErrorMap } from './potential-error-map.factory';
import { UserListResolver } from './user-list.resolver';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: UserListContainerComponent,
    resolve: {
      search: UserListResolver
    },
    data: {
      errorMap: createUserListErrorMap()
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserListRoutingModule {}
