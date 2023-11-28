import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { NgModule } from '@angular/core';
import {
  CheckAuthorityUserContainerComponent,
  SetAuthorityUserFormContainerComponent,
  SetAuthorityUserSuccessComponent,
  SetAuthorityUserWizardComponent
} from '@authority-setting/component';
import { AuthoritySettingRoutePathsModel } from '@authority-setting/model/authority-setting-route-paths.model';
import { CancelAuthoritySettingComponent } from '@authority-setting/component/cancel-authority-setting';

const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    component: SetAuthorityUserWizardComponent,
    children: [
      {
        path: '',
        component: SetAuthorityUserFormContainerComponent
      },
      {
        path: AuthoritySettingRoutePathsModel.CHECK_UPDATE_REQUEST,
        component: CheckAuthorityUserContainerComponent
      }
    ]
  },
  {
    path: AuthoritySettingRoutePathsModel.UPDATE_REQUEST_SUCCESS,
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    component: SetAuthorityUserSuccessComponent
  },
  {
    path: AuthoritySettingRoutePathsModel.CANCEL_UPDATE_REQUEST,
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    component: CancelAuthoritySettingComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AuthoritySettingRoutingModule {}
