import { LoginGuard } from '@shared/guards';
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { IssuanceAllocationOverviewComponent } from './components';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: IssuanceAllocationOverviewComponent,
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
export class IssuanceAllocationStatusRoutingModule {}
