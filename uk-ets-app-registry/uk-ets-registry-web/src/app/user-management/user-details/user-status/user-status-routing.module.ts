import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  ConfirmUserStatusActionContainerComponent,
  SelectUserStatusActionContainerComponent,
} from './components';

import { LoginGuard } from '@shared/guards';
import { UserStatusActionTypesGuard } from './guards';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard, UserStatusActionTypesGuard],
    children: [
      {
        path: '',
        canActivate: [LoginGuard],
        component: SelectUserStatusActionContainerComponent,
      },
      {
        path: 'confirm',
        canActivate: [LoginGuard],
        component: ConfirmUserStatusActionContainerComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserStatusRoutingModule {}
