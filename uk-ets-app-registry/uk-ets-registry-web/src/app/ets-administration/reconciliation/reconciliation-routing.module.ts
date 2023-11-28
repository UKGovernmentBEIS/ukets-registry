import { LoginGuard } from '@shared/guards';
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { ReconciliationStartContainerComponent } from '@reconciliation-administration/components/reconciliation-start';
const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: ReconciliationStartContainerComponent,
    data: {
      headerVisible: true,
      actionsVisible: false,
      backToListVisible: false
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReconciliationRouteModule {}
