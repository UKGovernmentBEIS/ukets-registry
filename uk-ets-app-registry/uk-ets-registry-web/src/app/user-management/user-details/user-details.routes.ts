import { LoginGuard } from '@shared/guards';
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { UserDetailsContainerComponent } from './components';
import { UserStatusContainerComponent } from '@user-management/user-details/user-status/components/user-status-container/user-status-container.component';
import { UserHeaderGuard } from '@user-management/guards';
import { UserDetailsUpdateContainerComponent } from '@user-update/component/user-details-update';
import { UserDetailsUpdateWizardPathsModel } from '@user-update/model';
import { ClearUpdateUserDetailsGuard } from '@user-update/guards';
import { UserStatusActionTypesGuard } from '@user-management/user-details/user-status/guards';

export const routes: Routes = [
  {
    path: 'my-profile',
    canActivate: [LoginGuard, UserHeaderGuard],
    component: UserDetailsContainerComponent,
  },
  {
    path: ':urid',
    canActivate: [LoginGuard, UserHeaderGuard],
    component: UserDetailsContainerComponent,
  },
  {
    path: '',
    canActivate: [LoginGuard],
    component: UserDetailsUpdateContainerComponent,
    children: [
      {
        path: `:urid/${UserDetailsUpdateWizardPathsModel.BASE_PATH}`,
        canActivate: [UserHeaderGuard],
        canDeactivate: [ClearUpdateUserDetailsGuard],
        loadChildren: () =>
          import(
            './user-details-update-wizard/user-details-update-wizard.module'
          ).then((m) => m.UserDetailsUpdateWizardModule),
      },
    ],
  },
  {
    path: '',
    canActivate: [LoginGuard],
    component: UserStatusContainerComponent,
    children: [
      {
        path: ':urid/status',
        canActivate: [UserHeaderGuard],
        canDeactivate: [UserStatusActionTypesGuard],
        loadChildren: () =>
          import('./user-status/user-status.module').then(
            (m) => m.UserStatusModule
          ),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserDetailsRoutingModule {}
