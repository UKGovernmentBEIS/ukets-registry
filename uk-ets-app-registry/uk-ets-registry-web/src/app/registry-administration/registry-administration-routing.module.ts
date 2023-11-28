import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { NgModule } from '@angular/core';
import { AuthoritySettingRoutePathsModel } from '@authority-setting/model';
import { RegistryAdministrationContainerComponent } from '@registry-web/registry-administration/component';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: RegistryAdministrationContainerComponent
  },
  {
    path: `${AuthoritySettingRoutePathsModel.BASE_PAGE}`,
    loadChildren: () =>
      import('./authority-setting/authority-setting.module').then(
        m => m.AuthoritySettingModule
      )
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RegistryAdministrationRoutingModule {}
