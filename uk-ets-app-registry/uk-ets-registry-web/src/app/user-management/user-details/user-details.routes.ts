import { LoginGuard } from '@shared/guards';
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { UserDetailsContainerComponent } from './components';
import { UserStatusContainerComponent } from '@user-management/user-details/user-status/components/user-status-container/user-status-container.component';
import { UserDetailsUpdateContainerComponent } from '@user-update/component/user-details-update';
import { UserDetailsUpdateWizardPathsModel } from '@user-update/model';
import { ClearUpdateUserDetailsGuard } from '@user-update/guards';
import { UserStatusActionTypesGuard } from '@user-management/user-details/user-status/guards';
import { UserAgentFormContainerComponent } from '@user-agent/components';
import { clearUserAgentDetailsGuard } from '@user-agent/guards';
import { initUserAgentDetailsResolver } from '@user-agent/resolvers';
import { initUserDetailsResolver } from '@user-management/user-details/resolvers';
import { UserCrcFormContainerComponent } from '@user-crc/components';
import { initUserCrcDetailsResolver } from '@user-crc/resolvers';
import { clearUserCrcDetailsGuard } from '@user-crc/guards';

export const routes: Routes = [
  {
    path: 'my-profile',
    canActivate: [LoginGuard],
    component: UserDetailsContainerComponent,
    resolve: {
      header: initUserDetailsResolver,
    },
  },
  {
    path: ':urid',
    canActivate: [LoginGuard],
    component: UserDetailsContainerComponent,
    resolve: {
      header: initUserDetailsResolver,
    },
  },
  {
    path: '',
    canActivate: [LoginGuard],
    component: UserDetailsUpdateContainerComponent,
    children: [
      {
        path: `:urid/${UserDetailsUpdateWizardPathsModel.BASE_PATH}`,
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
        canDeactivate: [UserStatusActionTypesGuard],
        loadChildren: () =>
          import('./user-status/user-status.module').then(
            (m) => m.UserStatusModule
          ),
      },
    ],
  },
  {
    path: '',
    canActivate: [LoginGuard],
    component: UserAgentFormContainerComponent,
    children: [
      {
        path: ':urid/agent',
        canDeactivate: [clearUserAgentDetailsGuard],
        resolve: {
          userAgentDetails: initUserAgentDetailsResolver,
        },
        loadChildren: () =>
          import('./user-agent/user-agent.module').then(
            (m) => m.UserAgentModule
          ),
      },
    ],
  },
  {
    path: '',
    canActivate: [LoginGuard],
    component: UserCrcFormContainerComponent,
    children: [
      {
        path: ':urid/crc',
        canDeactivate: [clearUserCrcDetailsGuard],
        resolve: {
          userCrcDetails: initUserCrcDetailsResolver,
        },
        loadChildren: () =>
          import('./user-crc/user-crc.module').then((m) => m.UserCrcModule),
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class UserDetailsRoutingModule {}
